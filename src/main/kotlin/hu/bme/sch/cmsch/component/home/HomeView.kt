package hu.bme.sch.cmsch.component.home

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.achievement.AchievementEntityWrapperDto
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.component.leaderboard.TopListAsGroupEntryDto

data class HomeView(

    @JsonView(Preview::class)
    val news: List<NewsEntity> = listOf(),

    @JsonView(Preview::class)
    val upcomingEvents: List<EventEntity> = listOf(),

    @JsonView(Preview::class)
    val achievements: List<AchievementEntityWrapperDto> = listOf(),

    @JsonView(Preview::class)
    val leaderBoard: List<TopListAsGroupEntryDto>,

    )
