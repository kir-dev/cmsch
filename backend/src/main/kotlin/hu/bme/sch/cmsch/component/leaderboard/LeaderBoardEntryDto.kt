package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class LeaderBoardEntryDto(
    @field:JsonView(FullDetails::class)
    val name: String,

    @field:JsonView(FullDetails::class)
    val groupName: String? = null,

    @field:JsonView(FullDetails::class)
    val score: Int?,
)
