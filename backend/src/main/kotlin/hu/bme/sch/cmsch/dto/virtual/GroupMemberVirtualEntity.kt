package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class GroupMemberVirtualEntity (

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @property:GenerateOverview(visible = false, columnName = "Neptun kód", order = 2, centered = true)
    var neptun: String = "",

    @property:GenerateOverview(columnName = "Gárda", order = 3, centered = true)
    var guild: String = "",

    @property:GenerateOverview(columnName = "", order = 4, centered = true)
    var roleName: String = "",

) : IdentifiableEntity
