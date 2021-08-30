package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView

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
