package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/export")
@ConditionalOnBean(ApplicationComponent::class)
class ExportAdminController(
    private val adminMenuService: AdminMenuService,
    private val componentHandlerService: ComponentHandlerService,
    private val auditLogService: AuditLogService
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

        model.addAttribute("settings", componentHandlerService.components
            .associateWith { it.allSettings }
            .flatMap { component ->
                component.value
                    .filter { it.persist }
                    .map {
                    "hu.bme.sch.cmsch.${component.key.component}.${it.property}=" +
                            escapeNonAscii(it.getValue())
                                .replace("\r", "")
                                .replace("\n", "\\\n    ")
                }
            }
            .joinToString("\n")
        )

        model.addAttribute("user", user)

        return "exportSettings"
    }

    fun escapeNonAscii(input: String): String {
        val result = StringBuilder()
        input.forEach { char ->
            if (char.code in 0..127) {
                result.append(char)
            } else {
                result.append("\\u%04x".format(char.code))
            }
        }
        return result.toString()
    }

}
