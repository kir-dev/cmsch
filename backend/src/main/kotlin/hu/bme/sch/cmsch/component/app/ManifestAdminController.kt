package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_APP
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/manifest")
@ConditionalOnBean(ManifestComponent::class)
class ManifestAdminController(
    adminMenuService: AdminMenuService,
    component: ManifestComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    ManifestComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_APP,
    componentMenuName = "Manifest beállítások",
    componentMenuIcon = "image",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent::class.simpleName!!,
    componentMenuPriority = 28,
    menuService = menuService
)

