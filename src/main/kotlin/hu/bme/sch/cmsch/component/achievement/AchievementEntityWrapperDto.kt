package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

enum class AchievementStatus {
    NOT_SUBMITTED,
    SUBMITTED,
    REJECTED,
    ACCEPTED,
    NOT_LOGGED_IN
}

data class AchievementEntityWrapperDto(
    @JsonView(value = [ Preview::class, FullDetails::class ])
        val achievement: AchievementEntity,

    @JsonView(value = [ Preview::class, FullDetails::class ])
        val status: AchievementStatus,

    @JsonView(value = [ Preview::class, FullDetails::class ])
        val response: String
)
