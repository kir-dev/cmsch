package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateOverview

data class TopListAsUserEntryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(visible = false)
        var id: Int,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Felhasználó", order = 1)
        override var name: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Tankör", order = 2, centered = true)
        var groupName: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "BucketList", order = 3, centered = true)
        override var achievementScore: Int,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Riddle", order = 4, centered = true)
        override var riddleScore: Int,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        @property:GenerateOverview(columnName = "Totál", order = 5, centered = true)
        override var totalScore: Int,
) : TopListAbstractEntryDto
