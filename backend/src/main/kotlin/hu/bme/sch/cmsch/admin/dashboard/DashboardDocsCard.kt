package hu.bme.sch.cmsch.admin.dashboard

class DashboardDocsCard(
    override val id: Int,
    override val wide: Boolean,
    override val title: String,
    val description: String = "",
    val markdownContent: String
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}