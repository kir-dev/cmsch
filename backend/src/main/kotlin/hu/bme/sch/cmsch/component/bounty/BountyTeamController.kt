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
@RequestMapping("/admin/control/bounty-teams")
@ConditionalOnBean(BountyComponent::class)
class BountyTeamController(
    repo: BountyTeamRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<BountyTeamEntity>(
    "bounty-teams",
    BountyTeamEntity::class, ::BountyTeamEntity,
    "Csapat", "Csapatok",
    "A Fejvadászat csapatok listája",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_TEAMS,
    createPermission = StaffPermissions.PERMISSION_CREATE_BOUNTY_TEAMS,
    editPermission = StaffPermissions.PERMISSION_EDIT_BOUNTY_TEAMS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_BOUNTY_TEAMS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "groups",
    adminMenuPriority = 3,

    searchSettings = calculateSearchSettings<BountyTeamEntity>(false)
)
