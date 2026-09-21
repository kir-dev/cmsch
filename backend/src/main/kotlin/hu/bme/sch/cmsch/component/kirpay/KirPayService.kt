package hu.bme.sch.cmsch.component.kirpay

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Service
@ConditionalOnBean(KirPayComponent::class)
class KirPayService(
    private val kirPayComponent: KirPayComponent,
    private val webClientBuilder: WebClient.Builder
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var cachedLeaderboard: List<KirPayLeaderboardEntry>? = null
    private val leaderboardLock = Any()

    @Volatile
    private var cachedSessionCookie: String? = null
    private val sessionLock = Any()

    private val kirPayClient: WebClient by lazy {
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(15))
        webClientBuilder
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

    private fun login(): String? {
        return try {
            val response = kirPayClient.post()
                .uri("${kirPayComponent.kirPayBackendUrl}/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapOf(
                    "username" to kirPayComponent.kirPayBackendUsername,
                    "password" to kirPayComponent.kirPayBackendPassword,
                )))
                .retrieve()
                .toBodilessEntity()
                .block() ?: return null.also { log.error("Kir-Pay login returned no response") }
            response.headers["Set-Cookie"]
                ?.firstOrNull { it.startsWith("SESSION=") }
                ?.substringBefore(';')
                .also { if (it == null) log.error("Kir-Pay login response has no SESSION cookie") }
        } catch (e: Exception) {
            log.error("Failed to login to Kir-Pay backend", e)
            null
        }
    }

    private fun currentSessionCookie(): String? {
        cachedSessionCookie?.let { return it }
        synchronized(sessionLock) {
            cachedSessionCookie?.let { return it }
            return login()?.also { cachedSessionCookie = it }
        }
    }

    private fun reloginAfter401(staleCookie: String?): String? {
        synchronized(sessionLock) {
            // Another thread may have already refreshed the session while we waited for the lock
            cachedSessionCookie?.takeIf { it != staleCookie }?.let { return it }
            return login()?.also { cachedSessionCookie = it }
                .also { if (it == null) cachedSessionCookie = null }
        }
    }

    private fun <T> fetchWithSessionRetry(fetch: (sessionCookie: String) -> T): T? {
        val sessionCookie = currentSessionCookie() ?: return null
        return try {
            fetch(sessionCookie)
        } catch (e: WebClientResponseException.Unauthorized) {
            log.info("Kir-Pay session expired, re-authenticating")
            val freshCookie = reloginAfter401(sessionCookie) ?: return null
            fetch(freshCookie) // single retry, never loops
        }
    }

    private var lastRefreshedAt = 0L

    @Scheduled(fixedRate = 1000L * 60)
    fun tick() {
        val now = System.currentTimeMillis()
        if (now - lastRefreshedAt >= kirPayComponent.leaderboardRefreshIntervalMinutes * 60_000L) {
            refreshCache()
        }
    }

    private fun refreshCache() {
        if (!kirPayComponent.leaderboardEnabled) return
        lastRefreshedAt = System.currentTimeMillis()
        val refreshed = fetchConsumptionLeaderboard()
        synchronized(leaderboardLock) {
            cachedLeaderboard = refreshed
        }
    }

    fun getConsumptionLeaderboard(): List<KirPayLeaderboardEntry> {
        if (!kirPayComponent.leaderboardEnabled) return listOf()
        cachedLeaderboard?.let { return it }
        synchronized(leaderboardLock) {
            cachedLeaderboard?.let { return it }
            val result = fetchConsumptionLeaderboard()
            cachedLeaderboard = result
            return result
        }
    }


    fun getBalanceByEmail(email: String?): KirPayAccountWithVouchersView? {
        if (email.isNullOrBlank()) return null

        return try {
            fetchWithSessionRetry { cookie ->
                kirPayClient.get()
                    .uri("${kirPayComponent.kirPayBackendUrl}/terminal/account-by-email/{email}", email)
                    .header(HttpHeaders.COOKIE, cookie)
                    .retrieve()
                    .bodyToMono<KirPayAccountWithVouchersView>()
                    .block()
            }
        } catch (e: WebClientResponseException.NotFound) {
            null
        } catch (e: Exception) {
            log.error("Failed to fetch Kir-Pay balance", e)
            null
        }
    }

    private fun fetchConsumptionLeaderboard(): List<KirPayLeaderboardEntry> {
        return try {
            val response = fetchWithSessionRetry { cookie ->
                kirPayClient.get()
                    .uri("${kirPayComponent.kirPayBackendUrl}/admin/consumption-leaderboard?limit={limit}",
                        kirPayComponent.leaderboardMaxEntries)
                    .header(HttpHeaders.COOKIE, cookie)
                    .retrieve()
                    .bodyToMono<List<Map<String, Any>>>()
                    .block()
            } ?: listOf()

            return response.map {
                // Some gorgeous Kotlin code!
                val name = it["name"] as? String ?: return@map null
                val itemCount = it["itemCount"] as? Number ?: return@map null

                KirPayLeaderboardEntry(name = name, itemCount = itemCount.toLong())
            }.filterNotNull()

        } catch (e: Exception) {
            log.error("Failed to fetch Kir-Pay consumption leaderboard", e)
            listOf()
        }
    }

}
