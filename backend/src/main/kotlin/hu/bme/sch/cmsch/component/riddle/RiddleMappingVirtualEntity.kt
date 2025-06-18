package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class RiddleMappingVirtualEntity(

    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Kat. id", order = 1)
    var category: Int = 0,

    @property:GenerateOverview(columnName = "Riddle", order = 2)
    var riddle: String = "",

    @property:GenerateOverview(columnName = "Hint", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    var hintUsed: Boolean = false,

    @property:GenerateOverview(columnName = "Megoldva", order = 4, centered = true, renderer = OverviewType.BOOLEAN)
    var solved: Boolean = false,

    @property:GenerateOverview(columnName = "Átugorva", order = 4, centered = true, renderer = OverviewType.BOOLEAN)
    var skipped: Boolean = false,

    @property:GenerateOverview(columnName = "Próbálkozás", order = 5, centered = true)
    var attempt: Int = 0,

    @property:GenerateOverview(columnName = "Beadva", order = 6, centered = true, renderer = OverviewType.DATE)
    var timestamp: Long = 0

) : IdentifiableEntity
