package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class AchievementCategoryView(
    @JsonView(FullDetails::class)
        val categoryName: String,

    @JsonView(FullDetails::class)
        val achievements: List<AchievementEntityWrapperDto> = listOf(),
)
