package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.AchievementEntityWrapper
import hu.bme.sch.cmsch.dto.FullDetails

data class AchievementCategoryView(
        @JsonView(FullDetails::class)
        val categoryName: String,

        @JsonView(FullDetails::class)
        val achievements: List<AchievementEntityWrapper> = listOf(),
)
