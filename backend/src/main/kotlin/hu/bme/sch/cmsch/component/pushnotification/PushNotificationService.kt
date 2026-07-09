package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import tools.jackson.databind.ObjectMapper
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue

private const val MAX_CONCURRENT_SENDS = 100
private const val MAX_RETRIES = 4L
private val RETRY_INITIAL_BACKOFF = Duration.ofMillis(500)
private val RETRY_MAX_BACKOFF = Duration.ofSeconds(5)

@Service
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationService(
    @Value($$"${hu.bme.sch.cmsch.google.service-account-key}") accountKey: String,
    webClientBuilder: WebClient.Builder,
    objectMapper: ObjectMapper,
    private val transactionManager: PlatformTransactionManager,
    private val messagingTokenRepository: MessagingTokenRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val fcmWebClient = webClientBuilder.baseUrl("https://fcm.googleapis.com/v1/projects/").build()
    private val credentials = PushNotificationServiceCredentials(accountKey, objectMapper)


    fun sendToUser(userId: Int, notification: CmschNotification): Int {
        log.info("Sending notification {} to user: {}", notification, userId)
        val tokens = transactionManager.transaction(readOnly = true) {
            messagingTokenRepository.findAllTokensByUserId(userId)
        }
        return sendNotifications(tokens, notification)
    }

    fun sendToGroup(groupId: Int, notification: CmschNotification): Int {
        log.info("Sending notification {} to group: {}", notification, groupId)
        val tokens = transactionManager.transaction(readOnly = true) {
            messagingTokenRepository.findAllTokensByGroupId(groupId)
        }
        return sendNotifications(tokens, notification)
    }

    fun sendToAllUsers(notification: CmschNotification): Int {
        log.info("Sending notification {} to all users", notification)
        val tokens = transactionManager.transaction(readOnly = true) {
            messagingTokenRepository.findAllTokens()
        }
        return sendNotifications(tokens, notification)
    }

    fun sendToRole(role: RoleType, notification: CmschNotification): Int {
        log.info("Sending notification {} to users with role {}", notification, role.displayName)
        val tokens = transactionManager.transaction(readOnly = true) {
            messagingTokenRepository.findAllTokensByRole(role)
        }
        return sendNotifications(tokens, notification)
    }

    private fun getAccessToken(): String {
        return credentials.getAccessToken()
    }

    private fun sendNotifications(tokens: List<String>, notification: CmschNotification): Int {
        if (tokens.isEmpty()) return 0

        val accessToken = runCatching { getAccessToken() }.getOrElse {
            log.error("Failed to get FCM access token", it)
            return 0
        }

        val staleTokens = ConcurrentLinkedQueue<String>()

        val results = Flux.fromIterable(tokens)
            .flatMap({ token ->
                fcmWebClient.post()
                    .uri("{projectId}/messages:send", credentials.projectId)
                    .header("Authorization", "Bearer $accessToken")
                    .bodyValue(notification.toFcmRequest(token))
                    .retrieve()
                    .toBodilessEntity()
                    .retryWhen(
                        Retry.backoff(MAX_RETRIES, RETRY_INITIAL_BACKOFF)
                            .maxBackoff(RETRY_MAX_BACKOFF)
                            .filter(::isRetryable)
                            .doBeforeRetry { signal ->
                                log.error(
                                    "Retryable error sending to token {} (attempt {}), retrying",
                                    token, signal.totalRetries() + 1, signal.failure()
                                )
                            }
                    )
                    .thenReturn(SendResult.SUCCESS)
                    .onErrorResume { e ->
                        if (e is WebClientResponseException.NotFound) {
                            staleTokens.add(token)
                            Mono.just(SendResult.STALE)
                        } else {
                            log.error("Failed to send notification to token {}", token, e)
                            Mono.just(SendResult.FAILURE)
                        }
                    }
            }, MAX_CONCURRENT_SENDS)
            .collectList()
            .block() ?: emptyList()

        if (staleTokens.isNotEmpty()) {
            val deleted = transactionManager.transaction(readOnly = false) {
                messagingTokenRepository.deleteByTokenIn(staleTokens.toList())
            }
            log.info("Deleted {} stale messaging tokens", deleted)
        }

        val successCount = results.count { it == SendResult.SUCCESS }
        log.info("Successfully sent notification to {} out of {} devices", successCount, tokens.size)
        return successCount
    }

    private fun isRetryable(e: Throwable): Boolean = when (e) {
        is WebClientResponseException -> e.statusCode.is5xxServerError || e.statusCode.value() == 429
        else -> true
    }

    @Transactional(readOnly = false)
    fun addToken(userId: Int, token: String) {
        if (messagingTokenRepository.existsByUserIdAndToken(userId, token)) return

        log.debug("Inserting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.save(MessagingTokenEntity(userId = userId, token = token))
    }

    @Transactional(readOnly = false)
    fun deleteToken(userId: Int, token: String) {
        log.debug("Deleting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.deleteByUserIdAndToken(userId, token)
    }
}

private enum class SendResult {
    SUCCESS,
    STALE,
    FAILURE,
}
