package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.model.IdentifiableEntity

class TokenCollectorGroupVirtualEntity(

    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Tankör", order = 1)
    var name: String = ""

) : IdentifiableEntity
