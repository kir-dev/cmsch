package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class TrackGroupVirtualEntity(

        @property:GenerateOverview(columnName = "Tankör", order = 1)
        val id: String

)