package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

class UserGroupTokenCount (
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "Csoport ID", order = -1)
    override var id: Int = 0,
    @property:GenerateOverview(columnName = "Csoport név", order = 1)
    var groupName: String = "",
    @property:GenerateOverview(columnName = "Tokenek száma", order = 3)
    var tokenCount: Long = 0,
    @property:GenerateOverview(columnName = "Csoporttagok száma", order = 4)
    var memberCount: Long = 0

): IdentifiableEntity