package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class DebtsByGroupVirtualEntity(

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var groupName: String = "",

    @property:GenerateOverview(columnName = "Forgalom [JMF]", order = 2, centered = true)
    var total: Int = 0,

    @property:GenerateOverview(columnName = "Fizetetlen [JMF]", order = 3, centered = true)
    var notPayed: Int = 0,

    @property:GenerateOverview(columnName = "Lezáratlan [JMF]", order = 4, centered = true)
    var notFinished: Int = 0

) : IdentifiableEntity
