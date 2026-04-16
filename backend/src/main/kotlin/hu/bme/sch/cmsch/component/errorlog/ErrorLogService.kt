package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(ErrorLogComponent::class)
class ErrorLogService(
    private val errorLogComponent: ErrorLogComponent,
    private val errorLogRepository: ErrorLogRepository,
    private val clock: TimeService
) {

    @Transactional
    @Retryable(
        value = [DataIntegrityViolationException::class],
        maxRetries = 3,
        delay = 100,
        multiplier = 2.0,
        jitter = 25,
    )
    fun submit(message: String, stack: String, userAgent: String, href: String, role: RoleType) {
        if (!errorLogComponent.receiveReports) return

        val existingLog =
            errorLogRepository.findByMessageAndStackAndUserAgentAndHrefAndRole(message, stack, userAgent, href, role)

        if (existingLog.isPresent) {
            errorLogRepository.incrementCount(existingLog.get().id, clock.getTimeInSeconds())
        } else {
            val newLog = ErrorLogEntity(
                message = message,
                stack = stack,
                userAgent = userAgent,
                href = href,
                role = role,
            )
            errorLogRepository.save(newLog)
        }
    }

}
