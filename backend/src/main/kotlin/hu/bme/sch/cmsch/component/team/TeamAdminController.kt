package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImportService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/team")
@ConditionalOnBean(TeamComponent::class)
class TeamAdminController(
    adminMenuService: AdminMenuService,
    component: TeamComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    TeamComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_TEAM,
    "Csapatok",
    "Csapatok beállítások",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/join-requests")
@ConditionalOnBean(TeamComponent::class)
class TeamController(
    repo: TeamJoinRequestRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TeamComponent
) : AbstractAdminPanelController<TeamJoinRequestEntity>(
    repo,
    "join-requests", "Kérelme", "Kérelmek",
    "A csoport csatlakozási kérelmek",
    TeamJoinRequestEntity::class, ::TeamJoinRequestEntity, importService, adminMenuService, component,
    permissionControl = ControlPermissions.PERMISSION_CONTROL_TEAM,
    importable = true, adminMenuIcon = "group_add", adminMenuPriority = 2,
    controlMode = CONTROL_MODE_EDIT
)
