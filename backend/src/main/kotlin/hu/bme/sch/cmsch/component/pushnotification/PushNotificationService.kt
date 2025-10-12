package hu.bme.sch.cmsch.component.pushnotification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.model.RoleType
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.retry.RetryTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationService(
    private val messaging: FirebaseMessaging,
    private val messagingTokenRepository: MessagingTokenRepository,
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

    private fun sendNotifications(tokens: List<String>, notification: CmschNotification): Int {
        if (tokens.isEmpty()) return 0

        var tokensRemaining = tokens
        val sendResult = runCatching {
            retryTemplate.execute {
                log.debug("Attempting notification broadcast to {} devices...", tokensRemaining.size)

                // The token limit per multicast message is 500
                val failedTokens = tokensRemaining.chunked(500)
                    .flatMap { chunk ->
                        val message = MulticastMessage.builder().addAllTokens(chunk)
                        notification.addToMessage(message)

                        val result = runCatching { messaging.sendEachForMulticast(message.build()) }
                        val responses = result.getOrNull()?.responses ?: return@flatMap chunk
                        return@flatMap responses
                            .zip(chunk)
                            .filter { (response, _) -> !response.isSuccessful }
                            .map { (_, token) -> token }
                    }

                if (failedTokens.isEmpty()) return@execute
                tokensRemaining = failedTokens
                throw Exception() // try again
            }
        }

        if (sendResult.isFailure) {
            log.info("Failed to send notification {} to {} devices", notification, tokensRemaining.size)
            // Maybe purge old and invalidated tokens?
        }
        return tokens.size - tokensRemaining.size
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
