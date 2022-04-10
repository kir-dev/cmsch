package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class AchievementCategoryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        var name: String = "",

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var categoryId: Int = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var availableFrom: Long = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var availableTo: Long = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var sum: Int = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var approved: Int = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var rejected: Int = 0,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        var notGraded: Int = 0
)
