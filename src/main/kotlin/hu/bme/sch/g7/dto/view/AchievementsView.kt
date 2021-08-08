package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.AchievementEntityWrapper
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.dto.TopListEntryDto

data class AchievementsView(
        @JsonView(Preview::class)
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?

        @JsonView(Preview::class)
        val warningMessage: String = "",

        @JsonView(Preview::class)
        val groupScore: Int?,

        @JsonView(Preview::class)
        val highlighted: List<AchievementEntityWrapper> = listOf(),

        @JsonView(Preview::class)
        val achievements: List<AchievementEntityWrapper> = listOf(),

        @JsonView(Preview::class)
        val leaderBoard: List<TopListEntryDto>
)