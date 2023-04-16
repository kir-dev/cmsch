package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class CheckRatingVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var groupName: String = "",

    @property:GenerateOverview(columnName = "Pont", order = 2)
    var score: Int = 0,

    @property:GenerateOverview(columnName = "Kapható max", order = 3)
    var maxScore: Int = 0,

) : IdentifiableEntity
