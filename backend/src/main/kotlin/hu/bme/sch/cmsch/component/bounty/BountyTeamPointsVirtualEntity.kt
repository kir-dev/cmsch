package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class BountyTeamPointsVirtualEntity(
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(visible = false)
    var roundId: Int = 0,

    @property:GenerateOverview(columnName = "Kör", order = 1)
    var roundName: String = "",

    @property:GenerateOverview(columnName = "Csapat", order = 2)
    var groupName: String = "",

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Gyilkosságok [db]", order = 3, centered = true)
    var kills: Int = 0,

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Gyilkosság pontok", order = 4, centered = true)
    var killPoints: Long = 0,

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Túlélési pontok", order = 5, centered = true)
    var survivalPoints: Long = 0,

    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Összpontszám", order = 6, centered = true)
    var total: Long = 0,
) : IdentifiableEntity
