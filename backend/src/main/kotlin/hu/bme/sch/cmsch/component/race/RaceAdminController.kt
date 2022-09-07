package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_RACE
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/race")
@ConditionalOnBean(RaceComponent::class)
class RaceAdminController(
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    RaceComponent::class.java,
    component,
    PERMISSION_CONTROL_RACE,
    "Verseny",
    "Verseny testreszabása",
    menuService = menuService
)

