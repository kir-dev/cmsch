package hu.bme.sch.cmsch.component.proto

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/proto")
@ConditionalOnBean(ProtoComponent::class)
class ProtoController(
    repo: ProtoRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ProtoComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<ProtoEntity>(
    "proto",
    ProtoEntity::class, ::ProtoEntity,
    "Válasz", "Válaszok",
    "Bizonyos lekérésre milyen választ adjon az oldal. Az API a /api/proto/** alatt érhető el.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_PROTO,
    createPermission = StaffPermissions.PERMISSION_CREATE_PROTO,
    editPermission =   StaffPermissions.PERMISSION_EDIT_PROTO,
    deletePermission = StaffPermissions.PERMISSION_DELETE_PROTO,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "http",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<ProtoEntity>(false)
)
