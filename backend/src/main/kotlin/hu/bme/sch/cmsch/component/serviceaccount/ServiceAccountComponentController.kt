package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/service-accounts")
@ConditionalOnBean(ServiceAccountComponent::class)
class ServiceAccountComponentController(
    adminMenuService: AdminMenuService,
    component: ServiceAccountComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ServiceAccountComponent::class.java,
    component,
    ImplicitPermissions.PERMISSION_NOBODY,
    "Service Account",
    "Service Accountok testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService
)
