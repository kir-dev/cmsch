package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.model.AchievementEntity
import hu.bme.sch.g7.model.SubmittedAchievementEntity

data class SingleAchievementView(
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
    val achievement: AchievementEntity,
    val submission: SubmittedAchievementEntity? = null
)