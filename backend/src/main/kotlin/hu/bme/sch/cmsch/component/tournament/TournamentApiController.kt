package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TournamentComponent::class)
class TournamentApiController(
    private val tournamentComponent: TournamentComponent,
    private val tournamentService: TournamentService
) {
    @JsonView(Preview::class)
    @GetMapping("/tournament")
    @Operation(
        summary = "List all tournaments.",
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of tournaments")
    ])
    fun tournaments(): ResponseEntity<List<TournamentEntity>> {
        val tournaments = tournamentService.findAll()
        return ResponseEntity.ok(tournaments)
    }

}