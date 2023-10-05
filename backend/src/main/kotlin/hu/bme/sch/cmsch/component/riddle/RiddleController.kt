package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/riddles")
@ConditionalOnBean(RiddleComponent::class)
class RiddleController(
    repo: RiddleEntityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : OneDeepEntityPage<RiddleEntity>(
    "riddles",
    RiddleEntity::class, ::RiddleEntity,
    "Riddle", "Riddleök",
    "Képrejtvények kezelése.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_RIDDLES,
    createPermission = StaffPermissions.PERMISSION_CREATE_RIDDLES,
    editPermission =   StaffPermissions.PERMISSION_EDIT_RIDDLES,
    deletePermission = StaffPermissions.PERMISSION_DELETE_RIDDLES,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "task",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<RiddleEntity>(false)
)
