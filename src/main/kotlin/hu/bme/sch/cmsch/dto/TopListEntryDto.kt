package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview

data class TopListEntryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Tankör", order = 1)
        val name: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Pont", order = 2, centered = true)
        val score: Int
)
