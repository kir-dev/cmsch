package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/tournament-match")
@ConditionalOnBean(TournamentComponent::class)
class TournamentMatchController(
    private val matchRepository: TournamentMatchRepository,
    private val tournamentService: TournamentService,
    private val stageService: KnockoutStageService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TournamentComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService,
    env: Environment
) : TwoDeepEntityPage<MatchGroupDto, TournamentMatchEntity>(
    "tournament-match",
    MatchGroupDto::class,
    TournamentMatchEntity::class, ::TournamentMatchEntity,
    "Mérkőzés", "Mérkőzések",
    "A mérkőzések kezelése.",
    transactionManager,
    object : ManualRepository<MatchGroupDto, Int>() {
        override fun findAll(): Iterable<MatchGroupDto> {
            return tournamentService.getAggregatedMatchesByTournamentId()
        }
    },
    matchRepository,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    createPermission = StaffPermissions.PERMISSION_CREATE_TOURNAMENTS,
    editPermission = StaffPermissions.PERMISSION_EDIT_TOURNAMENTS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_TOURNAMENTS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "compare_arrows",
) {
    override fun fetchSublist(id: Int): Iterable<TournamentMatchEntity> {
        return stageService.getMatchesByStageTournamentId(id)
    }
}