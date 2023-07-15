package hu.bme.sch.cmsch.admin.dashboard

import hu.bme.sch.cmsch.controller.admin.ButtonAction

class DashboardButtonGroup(
    override val id: Int,
    override val title: String,
    override val wide: Boolean,
    val content: List<String>,
    val buttons: List<ButtonAction>
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}