package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.AchievementEntityWrapper
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.ToplistEntryDto

data class AchievementsView(
        @JsonView(FullDetails::class)
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?

        @JsonView(FullDetails::class)
        val groupScore: Int?,

        @JsonView(FullDetails::class)
        val highlighted: List<AchievementEntityWrapper> = listOf(),

        @JsonView(FullDetails::class)
        val achievements: List<AchievementEntityWrapper> = listOf(),

        @JsonView(FullDetails::class)
        val leaderBoard: List<ToplistEntryDto>
)