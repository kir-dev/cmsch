package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/export")
@ConditionalOnBean(ApplicationComponent::class)
class ExportAdminController(
    private val adminMenuService: AdminMenuService,
    private val componentHandlerService: ComponentHandlerService
) {

    private val view = "export"
    private val permissionControl = ControlPermissions.PERMISSION_CONTROL_APP

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent::class.simpleName!!, AdminMenuEntry(
                "Export minden",
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
            return "admin403"
        }

        model.addAttribute("settings", componentHandlerService.components
            .associateWith { it.allSettings }
            .flatMap { component ->
                component.value
                    .filter { it.persist }
                    .map {
                    "hu.bme.sch.cmsch.${component.key.component}.${it.property}=" +
                            it.getValue().replace("\r", "").replace("\n", "\\\n    ")
                }
            }
            .joinToString("\n")
        )

        model.addAttribute("user", user)

        return "exportSettings"
    }

}
