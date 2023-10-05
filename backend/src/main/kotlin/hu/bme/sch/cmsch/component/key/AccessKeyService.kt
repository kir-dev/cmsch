package hu.bme.sch.cmsch.component.key

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.TimeService
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

private const val ACCESS_KEY = "access-key"

@Service
@ConditionalOnBean(AccessKeyComponent::class)
open class AccessKeyService(
    private val accessKeyRepository: AccessKeyRepository,
    private val accessKeyComponent: AccessKeyComponent,
    private val auditLogService: AuditLogService,
    private val clock: TimeService,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun validateKey(user: CmschUser, key: String): AccessKeyResponse {
        if (key.isEmpty()) {
            auditLogService.fine(user, ACCESS_KEY, "user tried an empty key")
            log.info("User {} tried an empty key", user.userName)
            return AccessKeyResponse(success = false,
                reason = accessKeyComponent.invalidCodeErrorMessage.getValue(),
                refreshSession = false
            )
        }

        if (!accessKeyComponent.canOneUserUseMultiple.isValueTrue()
                && accessKeyRepository.findTop1ByUsedByUserId(user.id).isNotEmpty()) {

            auditLogService.fine(user, ACCESS_KEY, "user already used a key")
            log.info("User {} already used a key", user.userName)
            return AccessKeyResponse(success = false,
                reason = accessKeyComponent.youUsedErrorMessage.getValue(),
                refreshSession = false
            )
        }

        val keys = accessKeyRepository.findTop1ByAccessKey(key)
        if (keys.isEmpty()) {
            auditLogService.fine(user, ACCESS_KEY, "invalid key: $key")
            log.info("User {} invalid key: {}", user.userName, key)
            return AccessKeyResponse(success = false,
                reason = accessKeyComponent.invalidCodeErrorMessage.getValue(),
                refreshSession = false
            )
        }
        val selectedKey = keys.first()
        if (selectedKey.usedByUserId != 0) {
            auditLogService.fine(user, ACCESS_KEY, "already used key: $key by ${selectedKey.usedByUserName}")
            log.info("User {} already used key: {} by user {}", user.userName, key, selectedKey.usedByUserName)
            return AccessKeyResponse(success = false,
                reason = accessKeyComponent.alreadyUsedErrorMessage.getValue(),
                refreshSession = false
            )
        }

        selectedKey.usedByUserId = user.id
        selectedKey.usedByUserName = user.userName
        selectedKey.usedAt = clock.getTimeInSeconds()

        var changed = false
        var logMessage = ""
        val userEntity = userRepository.findById(user.id).orElseThrow()
        if (selectedKey.setGroup) {
            val group = groupRepository.findByName(selectedKey.groupName)
            if (group.isEmpty) {
                log.warn("User {} group {} does not exists for access key {}",
                    userEntity.userName, selectedKey.groupName, key)
            } else {
                userEntity.group = group.orElseThrow()
                userEntity.groupName = group.orElseThrow().name
                changed = true
                logMessage += "group:${userEntity.groupName} "
            }
        }
        if (selectedKey.setRole) {
            userEntity.role = selectedKey.roleType
            changed = true
            logMessage += "role:${userEntity.role}"
        }

        log.info("User {} used key {} and changed {}", userEntity.userName, key, logMessage)
        accessKeyRepository.save(selectedKey)
        userRepository.save(userEntity)
        return AccessKeyResponse(success = true, reason = "", refreshSession = changed)
    }

}