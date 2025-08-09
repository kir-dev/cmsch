package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.team.TeamService
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TournamentComponent::class)
class TournamentApiController(
    private val tournamentComponent: TournamentComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
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
    fun listTournaments(auth: Authentication?): ResponseEntity<List<TournamentPreviewView>> {
        val user = auth?.getUserOrNull()
        if (!tournamentComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(listOf())
        if (!tournamentComponent.showTournamentsAtAll) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }

        return ResponseEntity.ok(tournamentService.listAllTournaments())
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
        @PathVariable tournamentId: Int,
        auth: Authentication?
    ): ResponseEntity<OptionalTournamentView>{
        val user = auth?.getUserOrNull()
        if (!tournamentComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return tournamentService.showTournament(tournamentId, user)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @PostMapping("/tournament/register")
    fun registerTeam(
        @RequestBody tournamentJoinDto: TournamentJoinDto,
        auth: Authentication?
    ): TournamentJoinStatus {
        val user = auth?.getUserOrNull()
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS

        val tournament = tournamentService.findById(tournamentJoinDto.id)
            ?: return TournamentJoinStatus.TOURNAMENT_NOT_FOUND
        return when (tournament.participantType) {
            OwnershipType.GROUP -> tournamentService.teamRegister(tournament, user)
            OwnershipType.USER -> tournamentService.userRegister(tournament, user)
        }
    }

}