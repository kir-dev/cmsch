package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.*


const val AUDIT_LOG = "AUDIT_LOG"

@Service
class AuditLogService(
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val zone = ZoneId.of("Europe/Budapest")
    private var targetFile = getTargetFile()
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @Scheduled(cron = "0 1 0 * * ?", zone = "Europe/Budapest")
    fun myScheduledFunction() {
        targetFile = getTargetFile()
    }

    private fun getTargetFile(): Path {
        val dateTime = ZonedDateTime.now(zone)
        val parent = Path.of(startupPropertyConfig.auditLog).toFile()
        if (!parent.exists())
            parent.mkdirs()

        val date = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val file = Path.of(startupPropertyConfig.auditLog, "$date.log")

        if (!file.exists()) {
            file.createFile()
            file.appendText("--- FILE BEGIN @ $date${System.lineSeparator()}")
        }
        return file
    }

    fun login(user: CmschUser, action: String) {
        write("LOGIN ", "login", "${user.internalId}@${user.userName} : $action")
    }

    fun edit(user: CmschUser, component: String, action: String) {
        write("EDIT  ", component, "${user.internalId}@${user.userName} : $action")
    }

    fun edit(user: CmschUser, component: String, from: String, to: String) {
        write("EDIT  ", component, "${user.internalId}@${user.userName} from: $from to: $to")
    }

    fun create(user: CmschUser, component: String, action: String) {
        write("CREATE", component, "${user.internalId}@${user.userName} : $action")
    }

    fun delete(user: CmschUser, component: String, action: String) {
        write("DELETE", component, "${user.internalId}@${user.userName} : $action")
    }

    fun fine(user: CmschUser, component: String, action: String) {
        write("FINE  ", component, "${user.internalId}@${user.userName} : $action")
    }

    fun system(component: String, action: String) {
        write("SYSTEM", component, action)
    }

    fun error(component: String, action: String) {
        write("ERROR ", component, action)
    }

    fun error(user: CmschUser, component: String, action: String) {
        write("ERROR ", component, "${user.internalId}@${user.userName} : $action")
    }

    fun admin403(user: CmschUser, component: String, module: String, permissionString: String) {
        write("UNAUTH", component, "Unauthorized request to $module, required permission: $permissionString")
    }

    private fun write(level: String, component: String, content: String) {
        val now = timeFormatter.format(ZonedDateTime.now(zone))
        val text = "$now - [$level][$component] $content${System.lineSeparator()}"
        synchronized(targetFile) {
            targetFile.appendText(text, StandardCharsets.UTF_8)
        }
    }

    fun readLog(fileName: String): String {
        if (targetFile.fileName.toString() == fileName) {
            synchronized(targetFile) {
                return targetFile.readText(StandardCharsets.UTF_8)
            }
        } else {
            return Path.of(startupPropertyConfig.auditLog, fileName).readText(StandardCharsets.UTF_8)
        }
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