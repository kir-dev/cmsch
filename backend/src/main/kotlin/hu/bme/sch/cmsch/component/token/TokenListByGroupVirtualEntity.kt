package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview

data class TokenListByGroupVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Tankör", order = 1)
    val groupName: String,

    @property:GenerateOverview(columnName = "Tokenek [db]", order = 2, centered = true)
    val tokens: Int,

)
