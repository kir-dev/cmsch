package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.model.AchievementEntity

enum class AchievementStatus {
    NOT_SUBMITTED,
    SUBMITTED,
    REJECTED,
    ACCEPTED
}

data class AchievementEntityWrapper(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        val achhievement: AchievementEntity,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val status: AchievementStatus
)