package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.GenerateOverview

data class ProductGroupVirtualEntity (

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Termék", order = 1)
    val name: String,

    @property:GenerateOverview(columnName = "Eladott mennyiség", order = 2, centered = true)
    val soldCount: Int,

)
