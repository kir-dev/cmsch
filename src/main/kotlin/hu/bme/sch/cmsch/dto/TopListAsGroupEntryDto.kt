package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview

data class TopListAsGroupEntryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Tankör", order = 1)
        override var name: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "BucketList", order = 2, centered = true)
        override var achievementScore: Int,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Riddle", order = 3, centered = true)
        override var riddleScore: Int,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Totál", order = 4, centered = true)
        override var totalScore: Int,
) : TopListAbstractEntryDto
