package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE

data class TokenVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Token", order = 1)
    val token: String,

    @property:GenerateOverview(columnName = "Típus", order = 2)
    val type: String,

    @property:GenerateOverview(columnName = "Beolvasva", order = 3, centered = true, renderer = OVERVIEW_TYPE_DATE)
    val timestamp: Long

)
