package hu.bme.sch.cmsch.component.signup

import hu.bme.sch.cmsch.admin.GenerateOverview

data class FormVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

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

)
