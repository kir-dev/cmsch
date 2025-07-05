package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.jvm.optionals.getOrNull

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

    outerControlActions = mutableListOf(
        ControlAction(
            "Match admin",
            "admin/{id}",
            "thumbs_up_down",
            StaffPermissions.PERMISSION_EDIT_RESULTS,
            200,
            false,
            "Mérkőzések eredményeinek felvitele"
        )
    )
) {
    override fun fetchSublist(id: Int): Iterable<TournamentMatchEntity> {
        return stageService.getUpcomingMatchesByTournamentId(id)
    }

    private val matchAdminControlActions = mutableListOf(
        ControlAction(
            "Eredmény felvitele",
            "score/{id}",
            "grade",
            StaffPermissions.PERMISSION_EDIT_RESULTS,
            200,
            false,
            "Mérkőzés eredményének felvitele"
        )
    )

    @GetMapping("/admin/{id}")
    fun matchAdminPage(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(StaffPermissions.PERMISSION_EDIT_RESULTS.validate(user).not()){
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/admin/$id", viewPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("view", view)

        val tournament = transactionManager.transaction(readOnly = true) {
            tournamentService.findById(id).getOrNull()?: return "redirect:/admin/control/tournament-match/"
        }
        val matches = transactionManager.transaction(readOnly = true) {
            stageService.getUpcomingMatchesByTournamentId(id)
        }

        model.addAttribute("tournament", tournament)
        model.addAttribute("matches", matches)
        model.addAttribute("controlActions", matchAdminControlActions.filter { it.permission.validate(user) })
        model.addAttribute("allControlActions", matchAdminControlActions)
        model.addAttribute("user", user)

        return "redirect:/admin/control/tournament-match/" //TODO
    }
}