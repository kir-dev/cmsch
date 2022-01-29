package hu.bme.sch.cmsch.dto.virtual

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_BOOLEAN
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE

data class RiddleMappingVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Kat. id", order = 1)
    val category: Int,

    @property:GenerateOverview(columnName = "Riddle", order = 2)
    val riddle: String,

    @property:GenerateOverview(columnName = "Hint", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    val hintUsed: Boolean,

    @property:GenerateOverview(columnName = "Megoldva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    val solved: Boolean,

    @property:GenerateOverview(columnName = "Próbálkozás", order = 5, centered = true)
    val attempt: Int,

    @property:GenerateOverview(columnName = "Beadva", order = 6, centered = true, renderer = OVERVIEW_TYPE_DATE)
    val timestamp: Long

)
