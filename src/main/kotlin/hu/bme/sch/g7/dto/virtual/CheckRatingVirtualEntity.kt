package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class CheckRatingVirtualEntity(

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Tankör", order = 1)
        val groupName: String,

        @property:GenerateOverview(columnName = "Pont", order = 2)
        val score: Int,

        @property:GenerateOverview(columnName = "Kapható max", order = 3)
        val maxScore: Int,

)
