package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/admin/control/component/popup")
class PopupComponentController(
    adminMenuService: AdminMenuService,
    component: PopupComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
): ComponentApiBase(
    adminMenuService,
    PopupComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_APP,
    componentMenuName = "Popup beállítások",
    componentMenuIcon = "flash_on",
    insertComponentCategory = false,
    componentCategory = ApplicationComponent.STYLING_CATEGORY,
    componentMenuPriority = 6,
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService
)