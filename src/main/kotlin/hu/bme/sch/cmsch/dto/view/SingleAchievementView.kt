package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.AchievementStatus
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.AchievementEntity
import hu.bme.sch.cmsch.model.SubmittedAchievementEntity

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
