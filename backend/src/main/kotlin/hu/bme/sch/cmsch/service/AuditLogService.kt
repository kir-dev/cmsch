package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.AuditLogByDayEntry
import hu.bme.sch.cmsch.model.AuditLogEntity
import hu.bme.sch.cmsch.repository.AuditLogRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


const val AUDIT_LOG = "AUDIT_LOG"

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository,
    private val clock: TimeService
) {

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @Transactional
    fun login(user: CmschUser, action: String) {
        write("LOGIN", "login", "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun edit(user: CmschUser, component: String, action: String) {
        write("EDIT", component, "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun edit(user: CmschUser, component: String, from: String, to: String) {
        edit(user, component, "from: $from to: $to")
    }

    @Transactional
    fun create(user: CmschUser, component: String, action: String) {
        write("CREATE", component, "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun delete(user: CmschUser, component: String, action: String) {
        write("DELETE", component, "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun fine(user: CmschUser, component: String, action: String) {
        write("FINE", component, "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun system(component: String, action: String) {
        write("SYSTEM", component, "SYSTEM", action)
    }

    @Transactional
    fun error(component: String, action: String) {
        write("ERROR", component, "SYSTEM", action)
    }

    @Transactional
    fun error(user: CmschUser, component: String, action: String) {
        write("ERROR", component, "${user.internalId}@${user.userName}", action)
    }

    @Transactional
    fun admin403(user: CmschUser, component: String, module: String, permissionString: String) {
        val content = "Unauthorized request to $module, required permission: $permissionString"
        write("UNAUTH", component, "${user.internalId}@${user.userName}", content)
    }

    @Transactional(readOnly = true)
    fun getLogTextForDay(dayTimestamp: Long): String =
        auditLogRepository
            .findAllLogsOnDay(dayTimestamp, clock.getZoneOffset())
            .joinToString("\r\n") { formatLogEntry(it) }

    @Transactional(readOnly = true)
    fun listDaysWithLogs(): List<AuditLogByDayEntry> =
        auditLogRepository
            .findAllDaysWithLogs(clock.getZoneOffset())

    @Transactional(readOnly = true)
    fun getAllLogText(): String =
        auditLogRepository
            .findAllOrderByTimestamp()
            .joinToString("\r\n") { formatLogEntry(it) }

    private fun formatLogEntry(entry: AuditLogEntity): String {
        val timestamp = Instant.ofEpochSecond(entry.timestamp)
        val timeText = timeFormatter.format(ZonedDateTime.ofInstant(timestamp, clock.timeZone))
        return "$timeText - [${entry.level}][${entry.component}] ${entry.actor}: ${entry.content}"
    }

    private fun write(level: String, component: String, actor: String, content: String) {
        val timestamp = clock.getTimeInSeconds()
        auditLogRepository.save(
            AuditLogEntity(
                level = level,
                actor = actor,
                component = component,
                content = content,
                timestamp = timestamp
            )
        )
    }

    @Component
    class ApplicationContextStartedListener(
        private var auditLogService: AuditLogService
    ) : ApplicationListener<ContextRefreshedEvent> {
        override fun onApplicationEvent(event: ContextRefreshedEvent) {
            auditLogService.system(AUDIT_LOG, "Application context started")
        }
    }

    @Component
    class ContextClosedListener(
        private var auditLogService: AuditLogService
    ) : ApplicationListener<ContextClosedEvent> {
        override fun onApplicationEvent(event: ContextClosedEvent) {
            auditLogService.system(AUDIT_LOG, "Application context closed")
        }
    }

}
