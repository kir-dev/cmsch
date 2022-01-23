package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview

class TokenCollectorGroupVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Tankör", order = 1)
    val name: String

)
