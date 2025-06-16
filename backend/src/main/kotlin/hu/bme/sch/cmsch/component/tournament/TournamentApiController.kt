package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.team.TeamService
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.GroupRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TournamentComponent::class)
class TournamentApiController(
    private val tournamentComponent: TournamentComponent,
    private val tournamentService: TournamentService,
    private val stageService: KnockoutStageService,
    private val groupRepository: GroupRepository,
) {
    @JsonView(Preview::class)
    @GetMapping("/tournament")
    @Operation(
        summary = "List all tournaments.",
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of tournaments")
    ])
    fun tournaments(): ResponseEntity<List<TournamentPreviewView>> {
        val tournaments = tournamentService.findAll()
        return ResponseEntity.ok(tournaments.map {
            TournamentPreviewView(
                it.id,
                it.title,
                it.description,
                it.location,
                it.status
            )
        })
    }


    @GetMapping("/tournament/{tournamentId}")
    @Operation(
        summary = "Get details of a tournament.",
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Details of the tournament"),
        ApiResponse(responseCode = "404", description = "Tournament not found")
    ])
    fun tournamentDetails(
        @PathVariable tournamentId: Int
    ): ResponseEntity<TournamentDetailedView>{
        val tournament = tournamentService.findById(tournamentId)
        if (tournament.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        val stages = stageService.findStagesByTournamentId(tournamentId)
        return ResponseEntity.ok(TournamentDetailedView(
            TournamentWithParticipants(
            tournament.get().id,
            tournament.get().title,
            tournament.get().description,
            tournament.get().location,
            tournament.get().participantCount,
            tournamentService.getParticipants(tournamentId),
            tournament.get().status
        ), stages.map { KnockoutStageDetailedView(
            it.id,
            it.name,
            it.level,
            it.participantCount,
            it.nextRound,
            it.status,
            stageService.findMatchesByStageId(it.id).map { MatchDto(
                it.id,
                it.gameId,
                it.kickoffTime,
                it.level,
                it.location,
                it.homeSeed,
                it.awaySeed,
                if(it.homeTeamId!=null) ParticipantDto(it.homeTeamId!!, it.homeTeamName) else null,
                if(it.awayTeamId!=null) ParticipantDto(it.awayTeamId!!, it.awayTeamName) else null,
                it.homeTeamScore,
                it.awayTeamScore,
                it.status
            ) }
        ) }))
    }

    @PostMapping("/tournament/{tournamentId}/register/{teamId}")
    @Operation(
        summary = "Register a team for a tournament.",
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Team registered successfully"),
        ApiResponse(responseCode = "401", description = "Not authorized, user must be group admin of the team"),
        ApiResponse(responseCode = "404", description = "Tournament or team not found"),
        ApiResponse(responseCode = "400", description = "Bad request, team already registered")
    ])
    fun registerTeam(
        @PathVariable tournamentId: Int,
        @PathVariable teamId: Int,
        user: CmschUser
    ): ResponseEntity<String> {
        val team = groupRepository.findById(teamId).getOrNull()
        if (team == null) {
            return ResponseEntity.notFound().build()
        }
        val tournament = tournamentService.findById(tournamentId).getOrNull()
        if (tournament == null) {
            return ResponseEntity.notFound().build()
        }

        if( tournamentService.isTeamRegistered(tournamentId, teamId)) {
            return ResponseEntity.badRequest().build()
        }

        if (user.groupId != team.id || user.role.value < RoleType.PRIVILEGED.value) {
            return ResponseEntity.status(401).build()
        }

        if (!tournament.joinable) {
            return ResponseEntity.badRequest().body("Tournament is not joinable")
        }

        val result = tournamentService.teamRegister(tournamentId, teamId, team.name)
        return if (result) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().body("Failed to register team")
        }
    }

}