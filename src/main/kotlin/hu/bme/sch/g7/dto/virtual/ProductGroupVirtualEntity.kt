package hu.bme.sch.g7.dto.virtual

import hu.bme.sch.g7.admin.GenerateOverview

data class ProductGroupVirtualEntity (

        @property:GenerateOverview(visible = false)
        val id: Int,

        @property:GenerateOverview(columnName = "Termék", order = 1)
        val name: String,

        @property:GenerateOverview(columnName = "Eladott mennyiség", order = 2, centered = true)
        val soldCount: Int,

)