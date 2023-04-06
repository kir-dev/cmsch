package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class ProductGroupVirtualEntity (

    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Termék", order = 1)
    var name: String = "",

    @property:GenerateOverview(columnName = "Eladott mennyiség", order = 2, centered = true)
    var soldCount: Int = 0,

) : IdentifiableEntity
