package hu.bme.sch.cmsch.admin

import hu.bme.sch.cmsch.controller.admin.ButtonAction

class DashboardButtonGroup(
    val wide: Boolean,
    val title: String,
    val content: List<ButtonAction>
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}