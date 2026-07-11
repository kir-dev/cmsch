package hu.bme.sch.cmsch.component.support

import tools.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/support-schedule")
@ConditionalOnBean(SupportComponent::class)
class SupportScheduleAdminController(
    repo: SupportScheduleRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: SupportComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    env: Environment,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService
) : OneDeepEntityPage<SupportScheduleEntity>(
    "support-schedule",
    SupportScheduleEntity::class, ::SupportScheduleEntity,
    "Beosztás", "Ügyfélszolgálat beosztás",
    "Ügyfélszolgálatos beosztás kezelése - ki mikor érhető el a megkeresések megválaszolásához.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS,
    createPermission = StaffPermissions.PERMISSION_MAKE_SCHEDULE,
    editPermission =   StaffPermissions.PERMISSION_MAKE_SCHEDULE,
    deletePermission = StaffPermissions.PERMISSION_MAKE_SCHEDULE,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "schedule",
    adminMenuPriority = 2,
    searchSettings = calculateSearchSettings<SupportScheduleEntity>(false)
)
