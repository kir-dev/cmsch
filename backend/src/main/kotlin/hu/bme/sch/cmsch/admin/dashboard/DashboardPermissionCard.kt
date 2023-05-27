package hu.bme.sch.cmsch.admin.dashboard

class DashboardPermissionCard(
    val title: String = "Jogosultságok",
    val permission: String,
    val description: String = "Ez a jog szükséges ennek az oldalnak az módosításához és olvasásához.",
    val icon: String = "local_police",
    val wide: Boolean,
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}