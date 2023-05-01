package hu.bme.sch.cmsch.component.bmejegy

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/bmejegy")
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyComponentController(
    adminMenuService: AdminMenuService,
    component: BmejegyComponent,
    menuService: MenuService,
    private val bmejegyTimer: BmejegyTimer,
    auditLogService: AuditLogService
) : ComponentApiBase(
    adminMenuService,
    BmejegyComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_BMEJEGY,
    "BME JEGY",
    "Jegyek testreszabása",
    auditLogService = auditLogService,
    menuService = menuService
) {

    // FIXME: Add buttons to do this
    @GetMapping("/action/clean")
    fun actionClean(auth: Authentication?): String {
        if (auth?.getUserOrNull()?.role?.isAdmin == true) {
            bmejegyTimer.clean()
        }
        return "redirect:/admin/control/component/bmejegy"
    }

}


