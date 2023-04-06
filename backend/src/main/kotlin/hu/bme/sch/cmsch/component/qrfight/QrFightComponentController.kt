package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.component.task.*
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/qrFight")
@ConditionalOnBean(QrFightComponent::class)
class QrFightComponentController(
    adminMenuService: AdminMenuService,
    component: QrFightComponent,
    menuService: MenuService,
    private val qrFightService: QrFightService
) : ComponentApiBase(
    adminMenuService,
    QrFightComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_QR_FIGHT,
    "QR Fight",
    "QR Fight beállítások",
    menuService = menuService
) {

    // FIXME: Add button
    @GetMapping("/execute-towers")
    fun forceExecuteTowers(auth: Authentication): String {
        if (auth.getUserFromDatabaseOrNull()?.isSuperuser() != true) {
            return "redirect:/admin/control/component/qrFight/settings?error=filed-to-execute"
        }
        qrFightService.executeTowerTimer()
        return "redirect:/admin/control/component/qrFight/settings?status=executed"
    }

}
