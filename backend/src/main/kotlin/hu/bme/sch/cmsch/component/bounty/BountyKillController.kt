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
@RequestMapping("/admin/control/bounty-kills")
@ConditionalOnBean(BountyComponent::class)
class BountyKillController(
    repo: BountyKillRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<BountyKillEntity>(
    "bounty-kills",
    BountyKillEntity::class, ::BountyKillEntity,
    "Gyilkosság", "Gyilkosságok",
    "A Fejvadászat gyilkosságok listája",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_KILLS,
    createPermission = StaffPermissions.PERMISSION_CREATE_BOUNTY_KILLS,
    editPermission = StaffPermissions.PERMISSION_EDIT_BOUNTY_KILLS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_BOUNTY_KILLS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "my_location",
    adminMenuPriority = 4,

    searchSettings = calculateSearchSettings<BountyKillEntity>(false)
)
