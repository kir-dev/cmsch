package hu.bme.sch.cmsch.component.bmejegy

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/bmejegy")
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyAdminController(
    adminMenuService: AdminMenuService,
    component: BmejegyComponent,
    menuService: MenuService,
    private val bmejegyTimer: BmejegyTimer
) : ComponentApiBase(
    adminMenuService,
    BmejegyComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_BMEJEGY,
    "BME JEGY",
    "Jegyek testreszabása",
    menuService = menuService
) {

    @GetMapping("/action/clean")
    fun actionClean(auth: Authentication?): String {
        if (auth?.getUserOrNull()?.role?.isAdmin == true) {
            bmejegyTimer.clean()
        }
        return "redirect:/admin/control/component/bmejegy"
    }

}

@Controller
@RequestMapping("/admin/control/bmejegy-tickets")
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyTicketsController(
    repo: BmejegyRecordRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BmejegyComponent
) : AbstractAdminPanelController<BmejegyRecordEntity>(
    repo,
    "bmejegy-tickets", "Jegy", "Jegyek",
    "BMEJEGY jegyek kezelése",
    BmejegyRecordEntity::class, ::BmejegyRecordEntity, importService, adminMenuService, component,
    permissionControl = StaffPermissions.PERMISSION_EDIT_BME_TICKET,
    importable = true, adminMenuIcon = "local_activity",
    allowedToPurge = true
)



