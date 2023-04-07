package hu.bme.sch.cmsch.admin

class DashboardCard(
    override val type: String,
    val wide: Boolean,
    val title: String,
    val description: String = "",
    val content: List<String>
) : DashboardComponent