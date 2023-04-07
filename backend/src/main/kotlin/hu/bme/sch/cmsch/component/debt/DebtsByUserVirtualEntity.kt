package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class DebtsByUserVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    var username: String = "",

    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
    var groupName: String = "",

    @property:GenerateOverview(columnName = "Totál [JMF]", order = 3, centered = true)
    var total: Int = 0,

    @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 4, centered = true)
    var notPayed: Int = 0,

    @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 5, centered = true)
    var notFinished: Int = 0

) : IdentifiableEntity
