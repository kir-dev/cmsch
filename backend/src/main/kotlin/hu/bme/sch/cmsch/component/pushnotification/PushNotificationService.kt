package hu.bme.sch.cmsch.component.pushnotification

import com.google.auth.oauth2.GoogleCredentials
import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.model.RoleType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity

@Service
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationService(
    private val credentials: GoogleCredentials,
    private val projectId: String,
    private val fcmWebClient: WebClient,
    private val messagingTokenRepository: MessagingTokenRepository,
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
                            true
                        }.getOrElse { e ->
                            log.error("Failed to send notification to token {}", token, e)
                            false
                        }
                    }
                }.awaitAll()
            }
        }

        val successCount = results.count { it }
        log.info("Successfully sent notification to {} out of {} devices", successCount, tokens.size)
        successCount
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun addToken(userId: Int, token: String) {
        if (messagingTokenRepository.existsByUserIdAndToken(userId, token)) return

        log.debug("Inserting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.save(MessagingTokenEntity(userId = userId, token = token))
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun deleteToken(userId: Int, token: String) {
        log.debug("Deleting messaging token for user: {}, token: {}", userId, token)
        messagingTokenRepository.deleteByUserIdAndToken(userId, token)
    }
}
