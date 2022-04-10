package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class SingleAchievementView(

    @JsonView(FullDetails::class)
    // If null: achievement not found
    val achievement: AchievementEntity? = null,

    @JsonView(FullDetails::class)
    // If null: no submission
    val submission: SubmittedAchievementEntity? = null,

    @JsonView(FullDetails::class)
    val status: AchievementStatus = AchievementStatus.NOT_LOGGED_IN

)
