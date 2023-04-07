package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class RiddleStatsVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Beadó", order = 1)
    var owner: String = "",

    @property:GenerateOverview(columnName = "Elfogadott", order = 2, centered = true)
    var completed: Int = 0,

    @property:GenerateOverview(columnName = "Hintek felhasználva", order = 3, centered = true)
    var hintsUsed: Int = 0,

) : IdentifiableEntity
