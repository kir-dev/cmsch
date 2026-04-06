package hu.bme.sch.cmsch.component.pushnotification

import com.google.auth.oauth2.GoogleCredentials
import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.retry.RetryTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationService(
    private val credentials: GoogleCredentials,
    private val projectId: String,
    private val fcmWebClient: WebClient,
    private val messagingTokenRepository: MessagingTokenRepository,
    private val pushNotificationComponent: PushNotificationComponent,
    private val clock: TimeService,
    private val retryTemplate: RetryTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun sendToUser(userId: Int, notification: CmschNotification): Int {
        log.info("Sending notification {} to user: {}", notification, userId)
        val tokens = messagingTokenRepository.findAllTokensByUserId(userId)
        return sendNotifications(tokens, notification)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun sendToGroup(groupId: Int, notification: CmschNotification): Int {
        log.info("Sending notification {} to group: {}", notification, groupId)
        val tokens = messagingTokenRepository.findAllTokensByGroupId(groupId)
        return sendNotifications(tokens, notification)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun sendToAllUsers(notification: CmschNotification): Int {
        log.info("Sending notification {} to all users", notification)
        val tokens = messagingTokenRepository.findAllTokens()
        return sendNotifications(tokens, notification)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun sendToRole(role: RoleType, notification: CmschNotification): Int {
        log.info("Sending notification {} to users with role {}", notification, role.displayName)
        val tokens = messagingTokenRepository.findAllTokensByRole(role)
        return sendNotifications(tokens, notification)
    }

    private fun getAccessToken(): String {
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }

    private data class SendResult(val success: Boolean, val token: String, val invalidToken: Boolean = false)

    private fun sendNotifications(tokens: List<String>, notification: CmschNotification): Int = runBlocking {
        if (tokens.isEmpty()) return@runBlocking 0

        val accessToken = runCatching { getAccessToken() }.getOrElse {
            log.error("Failed to get FCM access token", it)
            return@runBlocking 0
        }

        val results = coroutineScope {
            tokens.chunked(100).flatMap { chunk ->
                chunk.map { token ->
                    async {
                        runCatching {
                            fcmWebClient.post()
                                .uri("{projectId}/messages:send", projectId)
                                .header("Authorization", "Bearer $accessToken")
                                .bodyValue(notification.toFcmRequest(token))
                                .retrieve()
                                .awaitBodilessEntity()
                            SendResult(success = true, token = token)
                        }.getOrElse { e ->
                            val invalidToken = e is WebClientResponseException.NotFound ||
                                    (e is WebClientResponseException.BadRequest && e.responseBodyAsString.contains("UNREGISTERED", ignoreCase = true))
                            if (invalidToken) {
                                log.warn("Invalid token detected, will be removed: {}", token)
                            } else {
                                log.warn("Failed to send notification to token {}: {}", token, e.message)
                            }
                            SendResult(success = false, token = token, invalidToken = invalidToken)
                        }
                    }
                }.awaitAll()
            }
        }

        val successCount = results.count { it.success }
        val invalidTokens = results.filter { it.invalidToken }.map { it.token }

        if (invalidTokens.isNotEmpty()) {
            log.info("Removing {} invalid tokens", invalidTokens.size)
            removeTokens(invalidTokens)
        }

        log.info("Successfully sent notification to {} out of {} devices", successCount, tokens.size)
        successCount
    }

    private fun removeTokens(tokens: List<String>) {
        try {
            messagingTokenRepository.deleteByTokenIn(tokens)
        } catch (e: Exception) {
            log.error("Failed to remove invalid tokens", e)
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun addToken(userId: Int, token: String) {
        val now = clock.getTimeInSeconds()
        val existing = messagingTokenRepository.findByUserIdAndToken(userId, token)

        if (existing.isPresent) {
            existing.get().updatedAt = now
            messagingTokenRepository.save(existing.get())
            return
        }

        log.debug("Inserting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.save(MessagingTokenEntity(
            userId = userId,
            token = token,
            createdAt = now,
            updatedAt = now
        ))
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun deleteToken(userId: Int, token: String) {
        log.debug("Deleting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.deleteByUserIdAndToken(userId, token)
    }

    @Scheduled(fixedRate = 86400000)
    fun cleanupStaleTokens() {
        val staleThreshold = pushNotificationComponent.tokenStaleDays.toLong() * 24 * 60 * 60
        val cutoff = clock.getTimeInSeconds() - staleThreshold
        try {
            val deleted = messagingTokenRepository.deleteByUpdatedAtBefore(cutoff)
            if (deleted > 0) {
                log.info("Cleaned up {} stale messaging tokens (older than {} days)", deleted, pushNotificationComponent.tokenStaleDays)
            }
        } catch (e: Exception) {
            log.error("Failed to cleanup stale tokens", e)
        }
    }
}
