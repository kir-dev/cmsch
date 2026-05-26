package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class DebtsByUserVirtualEntity(

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    var username: String = "",

    @property:GenerateOverview(columnName = "Csoport", order = 2, centered = true)
    var groupName: String = "",

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Totál [JMF]", order = 3, centered = true)
    var total: Int = 0,

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Fizetetlen [JMF]", order = 4, centered = true)
    var notPayed: Int = 0,

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Lezáratlan [JMF]", order = 5, centered = true)
    var notFinished: Int = 0

) : IdentifiableEntity
