package hu.bme.sch.cmsch.admin

class DashboardTableCard(
    override val type: String,
    val wide: Boolean,
    val title: String,
    val description: String = "",
    val header: List<String>,
    val content: List<List<String>>
) : DashboardComponent