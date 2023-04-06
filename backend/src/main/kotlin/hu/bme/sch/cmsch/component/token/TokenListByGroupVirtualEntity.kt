package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TokenListByGroupVirtualEntity(

    @property:GenerateOverview(visible = false)
    override var id: Int,

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    val groupName: String,

    @property:GenerateOverview(columnName = "Tokenek [db]", order = 2, centered = true)
    val tokens: Int,

) : IdentifiableEntity
