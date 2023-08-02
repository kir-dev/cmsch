package hu.bme.sch.cmsch.component.home

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.task.TaskEntityWrapperDto
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardAsGroupEntryDto

data class LegacyHomeView(

    @field:JsonView(Preview::class)
    val news: List<NewsEntity> = listOf(),

    @field:JsonView(Preview::class)
    val upcomingEvents: List<EventEntity> = listOf(),

    @field:JsonView(Preview::class)
    val tasks: List<TaskEntityWrapperDto> = listOf(),

    @field:JsonView(Preview::class)
    val leaderBoard: List<LeaderBoardAsGroupEntryDto>,

)
