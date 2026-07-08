package hu.bme.sch.cmsch.component.pushnotification

import io.jsonwebtoken.Jwts
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import tools.jackson.core.json.JsonReadFeature
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
private val TOKEN_VALIDITY = Duration.ofHours(1)
private val EXPIRY_SKEW = Duration.ofMinutes(1)

class PushNotificationServiceCredentials(
    accountKey: String,
    private val objectMapper: ObjectMapper,
) {
    private data class ServiceAccountKey(
        val clientEmail: String,
        val privateKey: String,
        val tokenUri: String,
        val projectId: String,
    )

    private data class TokenResponse(
        val accessToken: String,
        val expiresIn: Long,
    )

    private val key: ServiceAccountKey = "".let {
        JsonMapper.builder().configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true).build()
            .readValue<Map<String, Any>>(accountKey).let { node ->
                ServiceAccountKey(
                    projectId = node["project_id"]!!.toString(),
                    clientEmail = node["client_email"].toString(),
                    privateKey = node["private_key"].toString(),
                    tokenUri = node["token_uri"]?.toString() ?: "https://oauth2.googleapis.com/token",
                )
            }
    }

    private val privateKey: PrivateKey = parsePrivateKey(key.privateKey)
    private val tokenWebClient: WebClient = WebClient.builder().build()

    private val lock = ReentrantLock()
    private var accessToken: String? = null
    private var expiresAt: Instant = Instant.EPOCH

    val projectId: String
        get() = key.projectId

    fun getAccessToken(): String = lock.withLock {
        val current = accessToken
        if (current != null && Instant.now().isBefore(expiresAt.minus(EXPIRY_SKEW))) {
            return current
        }
        val response = fetchToken()
        accessToken = response.accessToken
        expiresAt = Instant.now().plusSeconds(response.expiresIn)
        response.accessToken
    }

    private fun fetchToken(): TokenResponse {
        val now = Instant.now()
        val assertion = Jwts.builder()
            .issuer(key.clientEmail)
            .claim("aud", key.tokenUri)
            .claim("scope", MESSAGING_SCOPE)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(TOKEN_VALIDITY)))
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact()

        val body = tokenWebClient.post()
            .uri(key.tokenUri)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(
                BodyInserters.fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .with("assertion", assertion)
            )
            .retrieve()
            .bodyToMono<String>()
            .block() ?: throw IllegalStateException("Empty token response from ${key.tokenUri}")

        val node = objectMapper.readTree(body)
        return TokenResponse(
            accessToken = node.get("access_token").asString(),
            expiresIn = node.get("expires_in")?.asLong() ?: TOKEN_VALIDITY.seconds,
        )
    }

    private fun parsePrivateKey(pem: String): PrivateKey {
        val cleaned = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim()
        val decoded = Base64.getDecoder().decode(cleaned)
        return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(decoded))
    }
}
