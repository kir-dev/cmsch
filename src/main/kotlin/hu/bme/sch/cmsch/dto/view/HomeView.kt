package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.achievement.AchievementEntityWrapperDto
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.TopListAsGroupEntryDto
import hu.bme.sch.cmsch.model.EventEntity
import hu.bme.sch.cmsch.model.NewsEntity

data class HomeView(

    @JsonView(Preview::class)
    val news: List<NewsEntity> = listOf(),

    @JsonView(Preview::class)
    val upcomingEvents: List<EventEntity> = listOf(),

    @JsonView(Preview::class)
    val achievements: List<AchievementEntityWrapperDto> = listOf(),

    @JsonView(Preview::class)
    val leaderBoard: List<TopListAsGroupEntryDto>,

    @JsonView(Preview::class)
    val leaderBoardVisible: Boolean,

    @JsonView(Preview::class)
    val leaderBoardFrozen: Boolean,

    )
