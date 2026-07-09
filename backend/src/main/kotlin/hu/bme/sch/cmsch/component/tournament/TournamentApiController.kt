package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TournamentComponent::class)
class TournamentApiController(
    private val tournamentComponent: TournamentComponent,
    private val tournamentService: TournamentService,
) {

    @GetMapping("/tournament")
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

    @PutMapping("/tournament/register")
    fun registerTeam(
        @RequestBody tournamentJoinDto: TournamentJoinDto,
        auth: Authentication?
    ): TournamentJoinStatus {
        val user = auth?.getUserOrNull()
            ?: return TournamentJoinStatus.INSUFFICIENT_PERMISSIONS
        return tournamentService.register(tournamentJoinDto.id, user)
    }

    @PutMapping("/tournament/unregister")
    fun unregisterTeam(
        @RequestBody tournamentJoinDto: TournamentJoinDto,
        auth: Authentication?
    ): TournamentCancelStatus {
        val user = auth?.getUserOrNull()
            ?: return TournamentCancelStatus.INSUFFICIENT_PERMISSIONS
        return tournamentService.unregister(tournamentJoinDto.id, user)
    }

}