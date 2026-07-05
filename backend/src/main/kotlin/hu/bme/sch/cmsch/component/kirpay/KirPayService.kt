package hu.bme.sch.cmsch.component.kirpay

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.*

@Service
@ConditionalOnBean(KirPayComponent::class)
class KirPayService(
    private val kirPayComponent: KirPayComponent,
    webClientBuilder: WebClient.Builder
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Volatile
    private var cachedLeaderboard: List<KirPayLeaderboardEntry>? = null
    private val leaderboardLock = Any()

    private val kirPayClient: WebClient by lazy {
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(15))
        webClientBuilder
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(kirPayComponent.kirPayBackendUrl)
            .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                kirPayComponent.kirPayBackendToken.toByteArray()
            ))
            .build()
    }

    @Scheduled(fixedRate = 1000L * 60 * 60 * 10)
    fun refreshCache() {
        if (!kirPayComponent.leaderboardEnabled) return
        val refreshed = fetchConsumptionLeaderboard()
        synchronized(leaderboardLock) {
            cachedLeaderboard = refreshed
        }
        log.info("Kir-Pay consumption leaderboard cache refreshed, {} entries", refreshed.size)
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

    fun getBalanceByEmail(email: String?): Long? {
        if (email.isNullOrBlank()) return null

        return try {
            val response = kirPayClient.get()
                .uri("/accounts-by-email/{email}", email)
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .block()

            response?.get("balance")?.let { (it as Number).toLong() }
        } catch (e: WebClientResponseException.NotFound) {
            null
        } catch (e: Exception) {
            log.error("Failed to fetch Kir-Pay balance", e)
            null
        }
    }

    private fun fetchConsumptionLeaderboard(): List<KirPayLeaderboardEntry> {
        return try {
            val response = kirPayClient.get()
                .uri("/consumption-leaderboard")
                .retrieve()
                .bodyToMono<List<Map<String, Any>>>()
                .block()
                ?: listOf()

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
