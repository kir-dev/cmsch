package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class TrackGroupVirtualEntity(


    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var name: String = ""

) : IdentifiableEntity
