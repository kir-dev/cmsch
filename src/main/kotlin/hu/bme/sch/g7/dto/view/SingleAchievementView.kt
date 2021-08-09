package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.model.AchievementEntity
import hu.bme.sch.g7.model.SubmittedAchievementEntity

data class SingleAchievementView(
        @JsonView(FullDetails::class)
        val warningMessage: String = "",

        @JsonView(FullDetails::class)
        // If null: achievement not found
        val achievement: AchievementEntity? = null,

        @JsonView(FullDetails::class)
        // If null: no submission
        val submission: SubmittedAchievementEntity? = null
)