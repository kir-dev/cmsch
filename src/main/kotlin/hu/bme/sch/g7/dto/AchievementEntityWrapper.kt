package hu.bme.sch.g7.dto

import hu.bme.sch.g7.model.AchievementEntity

enum class AchievementStatus {
    NOT_SUBMITTED,
    SUBMITTED,
    REJECTED,
    ACCEPTED
}

data class AchievementEntityWrapper(
    val achhievement: AchievementEntity,
    val status: AchievementStatus
)