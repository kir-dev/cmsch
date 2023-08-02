package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardEntry

data class TasksView(

    @field:JsonView(Preview::class)
    val score: Int?,

    @field:JsonView(Preview::class)
    val categories: List<TaskCategoryDto> = listOf(),

    @field:JsonView(Preview::class)
    val leaderBoard: List<LeaderBoardEntry>,

    @field:JsonView(Preview::class)
    val leaderBoardVisible: Boolean,

    @field:JsonView(Preview::class)
    val leaderBoardFrozen: Boolean

)
