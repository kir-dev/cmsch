package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImportService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/extra-menus")
@ConditionalOnBean(ApplicationComponent::class)
class ExtraMenuAdminController(
    repo: ExtraMenuRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ApplicationComponent
) : AbstractAdminPanelController<ExtraMenuEntity>(
    repo,
    "extra-menus", "Extra menü", "Extra menük",
    "Nem komponenshez vagy laphoz tartozó menü",
    ExtraMenuEntity::class, ::ExtraMenuEntity, importService, adminMenuService, component,
    permissionControl = ControlPermissions.PERMISSION_CONTROL_APP,
    importable = false, adminMenuIcon = "new_label",
    adminMenuPriority = 3
)

