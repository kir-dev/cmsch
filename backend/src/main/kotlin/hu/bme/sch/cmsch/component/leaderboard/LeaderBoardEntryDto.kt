package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class LeaderBoardEntryDto(
        @JsonView(FullDetails::class)
        val name: String,

        @JsonView(FullDetails::class)
        val score: Int?,
)
