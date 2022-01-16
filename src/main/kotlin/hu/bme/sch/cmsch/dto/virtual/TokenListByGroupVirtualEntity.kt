package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

data class TokenListByGroupVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Tankör", order = 1)
    val groupName: String,

    @property:GenerateOverview(columnName = "Tokenjeik [db]", order = 2, centered = true)
    val tokens: Int,

)
