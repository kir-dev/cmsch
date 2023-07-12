package hu.bme.sch.cmsch.component.home

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/home")
@ConditionalOnBean(HomeComponent::class)
class HomeComponentController(
    adminMenuService: AdminMenuService,
    component: HomeComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    HomeComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_HOME,
    componentCategoryName = "Kezdőlap",
    componentMenuName = "Beállítások",
    menuService = menuService
)
