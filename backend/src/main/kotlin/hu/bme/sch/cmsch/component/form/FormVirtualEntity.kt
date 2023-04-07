package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.model.IdentifiableEntity

data class FormVirtualEntity(

    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int,

    @property:GenerateOverview(columnName = "Cím", order = 1)
    val name: String,

    @property:GenerateOverview(columnName = "Limit", order = 2)
    val limit: Int,

    @property:GenerateOverview(columnName = "Beküldött", order = 3)
    val submitted: Int,

    @property:GenerateOverview(columnName = "Fizetve", order = 4)
    val accepted: Int,

    @property:GenerateOverview(columnName = "Visszautasított", order = 5)
    val rejected: Int,

    @property:GenerateOverview(columnName = "Elfogadva", order = 6)
    val validated: Int,

) : IdentifiableEntity
