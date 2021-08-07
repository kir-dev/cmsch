package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.GenerateOverview

data class TopListEntryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Tankör", order = 1)
        val name: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Pont", order = 2, centered = true)
        val score: Int
)