package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

data class TokenListByUserVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    val username: String,

    @property:GenerateOverview(columnName = "Tankör", order = 2, centered = true)
    val groupName: String,

    @property:GenerateOverview(columnName = "Tokenek [db]", order = 3, centered = true)
    val tokens: Int,

)
