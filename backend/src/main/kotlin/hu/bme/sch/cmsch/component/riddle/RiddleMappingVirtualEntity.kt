package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_BOOLEAN
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_DATE
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class RiddleMappingVirtualEntity(

    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Kat. id", order = 1)
    var category: Int = 0,

    @property:GenerateOverview(columnName = "Riddle", order = 2)
    var riddle: String = "",

    @property:GenerateOverview(columnName = "Hint", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var hintUsed: Boolean = false,

    @property:GenerateOverview(columnName = "Megoldva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var solved: Boolean = false,

    @property:GenerateOverview(columnName = "Próbálkozás", order = 5, centered = true)
    var attempt: Int = 0,

    @property:GenerateOverview(columnName = "Beadva", order = 6, centered = true, renderer = OVERVIEW_TYPE_DATE)
    var timestamp: Long = 0

) : IdentifiableEntity
