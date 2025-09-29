package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class CheckRatingVirtualEntity(

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var groupName: String = "",

    @property:GenerateOverview(columnName = "Pont", order = 2, renderer = OverviewType.NUMBER)
    var score: Int = 0,

    @property:GenerateOverview(columnName = "Kapható max", order = 3, renderer = OverviewType.NUMBER)
    var maxScore: Int = 0,

) : IdentifiableEntity
