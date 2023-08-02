package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class LeaderBoardView(

    @field:JsonView(FullDetails::class)
    val userScore: Int? = null,

    @field:JsonView(FullDetails::class)
    val userBoard: List<LeaderBoardEntryDto>? = null,

    @field:JsonView(FullDetails::class)
    val groupScore: Int? = null,

    @field:JsonView(FullDetails::class)
    val groupBoard: List<LeaderBoardEntryDto>? = null

)
