package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/tournament")
@ConditionalOnBean(TournamentComponent::class)
class TournamentComponentController(
    adminMenuService: AdminMenuService,
    component: TournamentComponent,
    private val menuService: MenuService,
    private val tournamentService: TournamentService,
    private val auditLogService: AuditLogService,
    private val storageService: StorageService,
    service: MenuService
) : ComponentApiBase(
    adminMenuService,
    TournamentComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_TOURNAMENT,
    "Tournament",
    "Tournament beállítások",
    auditLogService = auditLogService,
    menuService = menuService,
    storageService = storageService
) {

}