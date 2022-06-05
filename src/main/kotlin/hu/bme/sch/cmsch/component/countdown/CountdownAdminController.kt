package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_EVENTS
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_IMPRESSUM
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_EVENTS
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/countdown")
@ConditionalOnBean(CountdownComponent::class)
class CountdownAdminController(
    adminMenuService: AdminMenuService,
    component: CountdownComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    CountdownComponent::class.java,
    component,
    PERMISSION_CONTROL_IMPRESSUM,
    componentMenuName = "Visszaszámlálás",
    menuService = menuService,
    componentMenuIcon = "alarm",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 20,
)
