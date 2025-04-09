package hu.bme.sch.cmsch.component.serviceaccount

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/service-account")
@ConditionalOnBean(ServiceAccountComponent::class)
class ServiceAccountKeyController(
    repo: ServiceAccountKeyRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ServiceAccountComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService,
    env: Environment
) : OneDeepEntityPage<ServiceAccountKeyEntity>(
    "service-account",
    ServiceAccountKeyEntity::class, ::ServiceAccountKeyEntity,
    "API kulcs", "API kulcsok",
    "Automatizált hozzáféréshez API kulcsok",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    createPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    editPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    deletePermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "lock",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<ServiceAccountKeyEntity>(false)
)
