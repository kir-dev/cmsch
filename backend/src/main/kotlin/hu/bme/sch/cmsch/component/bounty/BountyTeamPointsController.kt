package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import tools.jackson.databind.ObjectMapper

@Controller
@RequestMapping("/admin/control/bounty-points")
@ConditionalOnBean(BountyComponent::class)
class BountyTeamPointsController(
    private val bountyTeamRepository: BountyTeamRepository,
    private val bountyKillRepository: BountyKillRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<BountyTeamPointsVirtualEntity>(
    "bounty-points",
    BountyTeamPointsVirtualEntity::class, ::BountyTeamPointsVirtualEntity,
    "Pontszám", "Pontszámok",
    "A Fejvadászat pontszámainak összesítése körönként és csapatonként",

    transactionManager,
    object : ManualRepository<BountyTeamPointsVirtualEntity, Int>() {

        override fun findAll(): MutableList<BountyTeamPointsVirtualEntity> {
            val killsByRoundAndGroup = bountyKillRepository.findAll().groupBy { it.roundId to it.killerGroupId }
            return bountyTeamRepository.findAll().map { team ->
                val kills = killsByRoundAndGroup[team.roundId to team.groupId].orEmpty()
                val killPoints = kills.sumOf { it.points }
                val survivalPoints = team.survivalPoints ?: 0L
                BountyTeamPointsVirtualEntity(
                    id = team.id,
                    roundId = team.roundId,
                    roundName = team.roundName,
                    groupName = team.groupName,
                    kills = kills.size,
                    killPoints = killPoints,
                    survivalPoints = survivalPoints,
                    total = killPoints + survivalPoints,
                )
            }
                .sortedWith(compareBy<BountyTeamPointsVirtualEntity> { it.roundId }.thenByDescending { it.total })
                .toMutableList()
        }

    },

    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_TEAMS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission = ImplicitPermissions.PERMISSION_NOBODY,
    deletePermission = ImplicitPermissions.PERMISSION_NOBODY,

    createEnabled = false,
    editEnabled = false,
    deleteEnabled = false,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "leaderboard",
    adminMenuPriority = 5,

    searchSettings = calculateSearchSettings<BountyTeamPointsVirtualEntity>(false)
)
