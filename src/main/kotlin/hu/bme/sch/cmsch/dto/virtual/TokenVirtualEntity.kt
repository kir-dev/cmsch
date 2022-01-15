package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

data class TokenVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Token", order = 1)
    val token: String,

    @property:GenerateOverview(columnName = "Típus", order = 2)
    val type: String,

)
