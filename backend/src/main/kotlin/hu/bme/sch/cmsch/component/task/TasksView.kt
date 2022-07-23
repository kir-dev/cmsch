package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.component.leaderboard.TopListAbstractEntry

data class TasksView(

    @JsonView(Preview::class)
    val score: Int?,

    @JsonView(Preview::class)
    val categories: List<TaskCategoryDto> = listOf(),

    @JsonView(Preview::class)
    val leaderBoard: List<TopListAbstractEntry>,

    @JsonView(Preview::class)
    val leaderBoardVisible: Boolean,

    @JsonView(Preview::class)
    val leaderBoardFrozen: Boolean

)
