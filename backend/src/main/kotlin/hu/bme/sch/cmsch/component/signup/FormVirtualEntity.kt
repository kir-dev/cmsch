package hu.bme.sch.cmsch.component.signup

import hu.bme.sch.cmsch.admin.GenerateOverview

data class FormVirtualEntity(

    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Cím", order = 1)
    val name: String,

    @property:GenerateOverview(columnName = "Limit", order = 1)
    val limit: Int,

    @property:GenerateOverview(columnName = "Beküldött", order = 1)
    val submitted: Int,

    @property:GenerateOverview(columnName = "Elfogadva", order = 1)
    val accepted: Int,

    @property:GenerateOverview(columnName = "Visszautasított", order = 1)
    val rejected: Int,

)
