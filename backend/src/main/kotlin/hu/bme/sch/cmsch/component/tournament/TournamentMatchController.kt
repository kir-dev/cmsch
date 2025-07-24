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
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Isolation
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    env: Environment,
    private val knockoutStageRepository: KnockoutStageRepository
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
        return stageService.getMatchesByStageTournamentId(id)
    }

    /*private val matchAdminControlActions = mutableListOf(
        ControlAction(
            "Eredmény felvitele",
            "score/{id}",
            "grade",
            StaffPermissions.PERMISSION_EDIT_RESULTS,
            200,
            false,
            "Mérkőzés eredményének felvitele"
        )
    )*/


    @GetMapping("/show/{id}")
    override fun show(
        @PathVariable id: Int,
        model: Model,
        auth: Authentication
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(StaffPermissions.PERMISSION_SHOW_TOURNAMENTS.validate(user).not()){
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/show/$id", viewPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("view", view)

        model.addAttribute("user", user)
        val match = transactionManager.transaction(readOnly = true) {
            matchRepository.findById(id).getOrNull() ?: return "redirect:/admin/control/tournament-match/"
        }
        val stage = match.stage()
        if (stage == null) {
            model.addAttribute("error", "A mérkőzés nem tartozik érvényes szakaszhoz.")
            return "error"
        }
        model.addAttribute("match", match)
        model.addAttribute("stage", stage)
        model.addAttribute("editMode", true)

        return "matchScore"
    }


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
            //stageService.getUpcomingMatchesByTournamentId(id)
            stageService.getMatchesByStageTournamentId(id)
        }

        model.addAttribute("tournament", tournament)
        model.addAttribute("matches", matches)
        model.addAttribute("user", user)

        return "matchAdmin"
    }

    @GetMapping("/score/{id}")
    fun scoreMatchPage(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(StaffPermissions.PERMISSION_EDIT_RESULTS.validate(user).not()){
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/score/$id", viewPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("user", user)

        val match = transactionManager.transaction(readOnly = true) {
            matchRepository.findById(id).getOrNull()?: return "redirect:/admin/control/tournament-match/"
        }
        val stage = match.stage()
        if (stage == null) {
            model.addAttribute("error", "A mérkőzés nem tartozik érvényes szakaszhoz.")
            return "error"
        }
        model.addAttribute("match", match)
        model.addAttribute("stage", stage)
        model.addAttribute("readOnly", false)

        return "matchScore"
    }

    @PostMapping("/score/{id}")
    fun scoreMatch(
        @PathVariable id: Int,
        @RequestParam allRequestParams: Map<String, String>,
        model: Model,
        auth: Authentication
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(StaffPermissions.PERMISSION_EDIT_RESULTS.validate(user).not()){
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /$view/score/$id", viewPermission.permissionString)
            return "admin403"
        }
        val match = matchRepository.findById(id).getOrNull()?: return "redirect:/admin/control/$view"
        val stage = stageService.findById(match.stageId) ?: return "redirect:/admin/control/$view"

        if (match.status == MatchStatus.FINISHED || match.status == MatchStatus.CANCELLED) {
            model.addAttribute("error", "A mérkőzés már befejeződött vagy elmaradt, nem lehet új eredményt rögzíteni.")
            return "error"
        }

        match.homeTeamScore = allRequestParams["homeTeamScore"]?.toIntOrNull()
        match.awayTeamScore = allRequestParams["awayTeamScore"]?.toIntOrNull()
        match.status = when(allRequestParams["matchStatus"]){
            "FINISHED" -> MatchStatus.FINISHED
            "IN_PROGRESS" -> MatchStatus.IN_PROGRESS
            else -> MatchStatus.NOT_STARTED
        }

        if (onEntityPreSave(match, auth)){
            transactionManager.transaction(isolation = TransactionDefinition.ISOLATION_READ_COMMITTED, propagation = TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
                matchRepository.save(match)
                if (match.status == MatchStatus.FINISHED) {
                    // If the match is finished, we need to update the stage and tournament
                    val winner = match.winner()
                    if (winner != null) {
                        val updatedSeeds = stageService.setSeeds(stage)
                        stage.seeds = updatedSeeds
                        stageService.calculateTeamsFromSeeds(stage)
                        knockoutStageRepository.save(stage)
                    }
                }
            }
        }

        return "redirect:/admin/control/tournament-match"
    }

}