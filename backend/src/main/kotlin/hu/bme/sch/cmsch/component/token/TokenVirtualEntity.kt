package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TokenVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Token", order = 1)
    var token: String = "",

    @property:GenerateOverview(columnName = "Típus", order = 2)
    var type: String = "",

    @property:GenerateOverview(columnName = "Pont", order = 3, centered = true)
    var score: Int = 0,

    @property:GenerateOverview(columnName = "Beolvasva", order = 4, centered = true, renderer = OVERVIEW_TYPE_DATE)
    var timestamp: Long = 0

) : IdentifiableEntity
