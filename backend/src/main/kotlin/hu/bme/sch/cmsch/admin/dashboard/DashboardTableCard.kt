package hu.bme.sch.cmsch.admin.dashboard

open class DashboardTableCard(
    override val id: Int,
    override val title: String,
    val description: String = "",
    val header: List<String>,
    val content: List<List<String>>,
    override val wide: Boolean = false,
    val exportable: Boolean = false,
) : DashboardComponent {

    override val type: String = javaClass.simpleName

    open fun fileName() = "export"

}