package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_APP
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/app")
@ConditionalOnBean(ApplicationComponent::class)
class ApplicationAdminController(
    adminMenuService: AdminMenuService,
    component: ApplicationComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    ApplicationComponent::class.java,
    component,
    PERMISSION_CONTROL_APP,
    "Admin",
    "Oldal beállítások",
    componentMenuIcon = "functions",
    menuService = menuService
)

