package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.admin.GenerateOverview

data class TrackGroupVirtualEntity(

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    val id: String

)
