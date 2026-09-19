package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import tools.jackson.databind.ObjectMapper

@Controller
@RequestMapping("/admin/control/bounty-rounds")
@ConditionalOnBean(BountyComponent::class)
class BountyRoundController(
    repo: BountyRoundRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<BountyRoundEntity>(
    "bounty-rounds",
    BountyRoundEntity::class, ::BountyRoundEntity,
    "Kör", "Körök", "A Fejvadászat körök kezelése",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_ROUNDS,
    createPermission = StaffPermissions.PERMISSION_CREATE_BOUNTY_ROUNDS,
    editPermission = StaffPermissions.PERMISSION_EDIT_BOUNTY_ROUNDS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_BOUNTY_ROUNDS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "event",
    adminMenuPriority = 2,

    searchSettings = calculateSearchSettings<BountyRoundEntity>(false)
)
