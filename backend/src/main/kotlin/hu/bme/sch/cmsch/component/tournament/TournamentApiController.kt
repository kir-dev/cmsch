package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TournamentComponent::class)
class TournamentApiController(
    private val tournamentComponent: TournamentComponent,
    private val tournamentService: TournamentService,
    private val stageService: KnockoutStageService
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

}