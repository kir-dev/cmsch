package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
@ConditionalOnBean(ErrorLogComponent::class)
class ErrorLogService(
    private val errorLogComponent: ErrorLogComponent,
    private val errorLogRepository: ErrorLogRepository,
    private val clock: TimeService
) {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    fun submit(message: String, stack: String, userAgent: String, href: String, role: RoleType) {
        if (!errorLogComponent.receiveReports.getValue()) return

        val existingLog = errorLogRepository
            .findByMessageAndStackAndUserAgentAndHrefAndRole(message, stack, userAgent, href, role).getOrNull()

        val logToSave = existingLog ?: ErrorLogEntity(
            message = message,
            stack = stack,
            userAgent = userAgent,
            href = href,
            role = role,
        )
        logToSave.apply {
            count = count + 1
            lastReportedAt = clock.getTimeInSeconds()
        }

        errorLogRepository.save(logToSave)
    }

}
