package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TokenPropertyVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Tulajdonos", order = 1)
    var owner: String = "",

    @property:GenerateOverview(columnName = "Beolvasva", order = 2, centered = true, renderer = OVERVIEW_TYPE_DATE)
    var timestamp: Long = 0

) : IdentifiableEntity
