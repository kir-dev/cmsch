package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.util.*

@Controller
@RequestMapping("/admin/control/export")
@ConditionalOnBean(ApplicationComponent::class)
class ExportAdminController(
    private val adminMenuService: AdminMenuService,
    private val componentHandlerService: ComponentHandlerService,
    private val auditLogService: AuditLogService,
    private val clock: TimeService
) {

    private val view = "export"
    private val permissionControl = ControlPermissions.PERMISSION_CONTROL_APP_EXPORT

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.DEVELOPER_CATEGORY, AdminMenuEntry(
                "Exportálás",
                "file_download",
                "/admin/control/${view}",
                30,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, "export", "GET /export", permissionControl.permissionString)
            return "admin403"
        }

        model.addAttribute("settings", generateProperties())
        model.addAttribute("user", user)
        model.addAttribute("permission", permissionControl.permissionString)

        return "exportSettings"
    }

    @ResponseBody
    @GetMapping("/properties")
    fun export(
        auth: Authentication,
        response: HttpServletResponse,
        @RequestParam(required = false, defaultValue = "false") useIsoEncoding: Boolean
    ): ByteArray {
        val user = auth.getUser()
        if (!permissionControl.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"application-live-${clock.getTime()}.properties\"")

        val charset = if (useIsoEncoding) StandardCharsets.ISO_8859_1 else StandardCharsets.UTF_8
        response.contentType = MediaType(MediaType.APPLICATION_OCTET_STREAM, charset).toString()
        return generateProperties().toByteArray(charset)
    }

    private fun generateProperties(): String {
        val properties = Properties()
        componentHandlerService.components
            .associateWith { it.allSettings }
            .flatMap { component ->
                component.value
                    .filter { it.persist }
                    .map { "hu.bme.sch.cmsch.${component.key.component}.${it.property}" to it.getValue() }
            }
            .forEach { properties.setProperty(it.first, it.second) }

        val stringWriter = StringWriter()
        properties.store(stringWriter, "Generated at ${clock.getTime()}")
        return stringWriter.toString()
    }

}
