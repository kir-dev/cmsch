package hu.bme.sch.cmsch.admin

class DashboardCard(
    val wide: Boolean,
    val title: String,
    val description: String = "",
    val content: List<String>
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}