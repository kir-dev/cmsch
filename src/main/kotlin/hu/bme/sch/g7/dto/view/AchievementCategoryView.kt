package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.AchievementEntityWrapper
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.dto.TopListEntryDto

data class AchievementCategoryView(
        @JsonView(FullDetails::class)
        val categoryName: String,

        @JsonView(FullDetails::class)
        val achievements: List<AchievementEntityWrapper> = listOf(),
)