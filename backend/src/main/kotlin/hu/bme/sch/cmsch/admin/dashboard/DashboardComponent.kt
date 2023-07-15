package hu.bme.sch.cmsch.admin.dashboard

interface DashboardComponent {

    val id: Int
    val title: String
    val type: String
    val wide: Boolean

}