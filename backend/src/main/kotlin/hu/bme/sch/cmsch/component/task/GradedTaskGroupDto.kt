package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class GradedTaskGroupDto(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Cím", order = 1)
    var taskName: String = "",

    @property:GenerateOverview(columnName = "OK", order = 2, centered = true)
    var approved: Int = 0,

    @property:GenerateOverview(columnName = "Nem OK", order = 3, centered = true)
    var rejected: Int = 0,

    @property:GenerateOverview(columnName = "Értékelésre vár", order = 4, centered = true)
    var notGraded: Int = 0

) : IdentifiableEntity
