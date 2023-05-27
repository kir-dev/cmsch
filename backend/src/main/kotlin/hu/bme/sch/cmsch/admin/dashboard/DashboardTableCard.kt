package hu.bme.sch.cmsch.admin.dashboard

class DashboardTableCard(
    val title: String,
    val description: String = "",
    val header: List<String>,
    val content: List<List<String>>,
    val wide: Boolean = false
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}