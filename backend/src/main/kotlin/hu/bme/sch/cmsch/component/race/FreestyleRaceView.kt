package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class FreestyleRaceView(

    @field:JsonView(FullDetails::class)
    val categoryName: String = "",

    @field:JsonView(FullDetails::class)
    val description: String = "",

    @field:JsonView(FullDetails::class)
    val board: List<FreestyleRaceEntryDto> = listOf(),

)
