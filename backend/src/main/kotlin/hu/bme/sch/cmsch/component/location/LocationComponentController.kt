package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/location")
@ConditionalOnBean(LocationComponent::class)
class LocationComponentController(
    adminMenuService: AdminMenuService,
    component: LocationComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    LocationComponent::class.java,
    component,
    ControlPermissions.PERMISSION_CONTROL_LOCATION,
    componentCategoryName = "Helymegosztás",
    componentMenuName = "Helymegosztás",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService
)
