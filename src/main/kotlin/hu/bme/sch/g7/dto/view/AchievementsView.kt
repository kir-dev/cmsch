package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.dto.AchievementEntityWrapper
import hu.bme.sch.g7.dto.ToplistEntryDto

data class AchievementsView(
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
    val groupScore: Int?,
    val highlighted: List<AchievementEntityWrapper> = listOf(),
    val achievements: List<AchievementEntityWrapper> = listOf(),
    val leaderBoard: List<ToplistEntryDto>
)