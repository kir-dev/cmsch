package hu.bme.sch.cmsch.admin.dashboard

import hu.bme.sch.cmsch.admin.IconStatus

data class StatusTableRow(
    val row: List<String>,
    val status: IconStatus = IconStatus.EMPTY
)

open class DashboardStatusTableCard(
    override val id: Int,
    override val title: String,
    val description: String = "",
    val header: List<String>,
    val content: List<StatusTableRow>,
    override val wide: Boolean = false,
    val exportable: Boolean = false,
) : DashboardComponent {

    override val type: String = javaClass.simpleName

    open fun fileName() = "export"

}