package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.component.task.*
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/qrFight")
@ConditionalOnBean(QrFightComponent::class)
class QrFightAdminController(
    adminMenuService: AdminMenuService,
    component: QrFightComponent,
    menuService: MenuService,
    private val qrFightService: QrFightService
) : ComponentApiBase(
    adminMenuService,
    QrFightComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_TASKS,
    "QR Fight",
    "QR Fight beállítások",
    menuService = menuService
) {

    @GetMapping("/execute-towers")
    fun forceExecuteTowers(auth: Authentication): String {
        if (auth.getUserFromDatabaseOrNull()?.isSuperuser() != true) {
            return "redirect:/admin/control/component/qrFight/settings?error=filed-to-execute"
        }
        qrFightService.executeTowerTimer()
        return "redirect:/admin/control/component/qrFight/settings?status=executed"
    }

}

@Controller
@RequestMapping("/admin/control/qr-levels")
@ConditionalOnBean(QrFightComponent::class)
class QrLevelController(
    repo: QrLevelRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: QrFightComponent
) : AbstractAdminPanelController<QrLevelEntity>(
    repo,
    "qr-levels", "Szint", "Szintek",
    "QR Fight szintek hozzáadása és szerkesztése",
    QrLevelEntity::class, ::QrLevelEntity, importService, adminMenuService, component,
    permissionControl = StaffPermissions.PERMISSION_EDIT_QR_FIGHT,
    importable = true, adminMenuIcon = "apartment"
)

@Controller
@RequestMapping("/admin/control/qr-towers")
@ConditionalOnBean(QrFightComponent::class)
class QrTowerController(
    repo: QrTowerRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: QrFightComponent
) : AbstractAdminPanelController<QrTowerEntity>(
    repo,
    "qr-towers", "Torony", "Tornyok",
    "QR Fight toronyok hozzáadása és szerkesztése",
    QrTowerEntity::class,
    ::QrTowerEntity, importService, adminMenuService, component,
    permissionControl = StaffPermissions.PERMISSION_EDIT_QR_FIGHT,
    importable = true, adminMenuPriority = 2, adminMenuIcon = "cell_tower"
)
