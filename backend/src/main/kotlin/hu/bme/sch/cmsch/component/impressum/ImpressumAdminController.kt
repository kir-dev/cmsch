package hu.bme.sch.cmsch.component.impressum

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
@RequestMapping("/admin/control/component/impressum")
@ConditionalOnBean(ImpressumComponent::class)
class ImpressumAdminController(
    adminMenuService: AdminMenuService,
    component: ImpressumComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    ImpressumComponent::class.java,
    component,
    PERMISSION_CONTROL_IMPRESSUM,
    componentMenuName = "Impresszum",
    menuService = menuService,
    componentMenuIcon = "alternate_email",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 21,
)
