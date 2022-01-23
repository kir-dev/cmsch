package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.AchievementCategoryDto
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.TopListAbstractEntryDto
import hu.bme.sch.cmsch.dto.TopListAsGroupEntryDto

data class AchievementsView(

    @JsonView(Preview::class)
    val score: Int?,

    @JsonView(Preview::class)
    val categories: List<AchievementCategoryDto> = listOf(),

    @JsonView(Preview::class)
    val leaderBoard: List<TopListAbstractEntryDto>,

    @JsonView(Preview::class)
    val leaderBoardVisible: Boolean,

    @JsonView(Preview::class)
    val leaderBoardFrozen: Boolean

)
