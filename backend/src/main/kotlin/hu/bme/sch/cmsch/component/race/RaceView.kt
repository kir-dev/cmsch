package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class RaceView(

    @JsonView(FullDetails::class)
    val place: Int? = null,

    @JsonView(FullDetails::class)
    val bestTime: Float? = null,

    @JsonView(FullDetails::class)
    val board: List<RaceEntryDto> = listOf(),

)
