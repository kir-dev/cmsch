package hu.bme.sch.cmsch.admin.dashboard

import hu.bme.sch.cmsch.controller.admin.ButtonAction

class DashboardButtonGroup(
    val title: String,
    val content: List<ButtonAction>,
    val wide: Boolean,
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}