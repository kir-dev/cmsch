package hu.bme.sch.cmsch.component.profile

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/profile")
@ConditionalOnBean(ProfileComponent::class)
class ProfileAdminController(
    adminMenuService: AdminMenuService,
    component: ProfileComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    ProfileComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Profil beállítások",
    componentMenuIcon = "account_circle",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 6,
    menuService = menuService
)
