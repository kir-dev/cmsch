package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE

data class TokenPropertyVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Tulajdonos", order = 1)
    val owner: String,

    @property:GenerateOverview(columnName = "Beolvasva", order = 2, centered = true, renderer = OVERVIEW_TYPE_DATE)
    val timestamp: Long

)
