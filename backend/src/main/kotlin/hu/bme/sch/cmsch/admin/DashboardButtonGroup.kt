package hu.bme.sch.cmsch.admin

import hu.bme.sch.cmsch.controller.admin.ButtonAction

class DashboardButtonGroup(
    override val type: String,
    val wide: Boolean,
    val title: String,
    val content: List<ButtonAction>
) : DashboardComponent