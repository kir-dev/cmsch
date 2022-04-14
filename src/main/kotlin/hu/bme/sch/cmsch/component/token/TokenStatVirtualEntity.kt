package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE

data class TokenStatVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Token", order = 1)
    val token: String,

    @property:GenerateOverview(columnName = "Típus", order = 2)
    val type: String,

    @property:GenerateOverview(columnName = "Beolvasás", order = 3, centered = true)
    val count: Int

)
