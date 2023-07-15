package hu.bme.sch.cmsch.admin.dashboard

class DashboardCard(
    override val id: Int,
    override val wide: Boolean,
    override val title: String,
    val description: String = "",
    val content: List<String>
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}