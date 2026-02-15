package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TinderCommunityVirtualEntity(
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int,

    @property:GenerateOverview(columnName = "Név", order = 1)
    val name: String,

    @property:GenerateOverview(columnName = "Jobbra húzva", order = 2)
    val likes: Int,

    @property:GenerateOverview(columnName = "Balra húzva", order = 3)
    val dislikes: Int,

) : IdentifiableEntity