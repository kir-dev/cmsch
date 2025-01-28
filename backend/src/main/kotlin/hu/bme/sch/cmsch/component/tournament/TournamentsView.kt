package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class TournamentsView (
    @field:JsonView(value = [Preview::class, FullDetails::class])
    val tournaments: List<TournamentEntity>
)
