package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/login")
@ConditionalOnBean(LoginComponent::class)
class LoginAdminController(
    adminMenuService: AdminMenuService,
    component: LoginComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    LoginComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Auth beállítások",
    componentMenuIcon = "login",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 6,
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/component/unit-scope")
@ConditionalOnBean(LoginComponent::class)
class UnitScopeAdminController(
    adminMenuService: AdminMenuService,
    component: UnitScopeComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    UnitScopeComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_PROFILE,
    componentMenuName = "Jogviszony beállítások",
    componentMenuIcon = "verified",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 7,
    menuService = menuService
)
