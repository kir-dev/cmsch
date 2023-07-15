package hu.bme.sch.cmsch.admin.dashboard

class DashboardPermissionCard(
    override val id: Int,
    override val title: String = "Jogosultságok",
    val permission: String,
    val description: String = "Ez a jog szükséges ennek az oldalnak az módosításához és olvasásához.",
    val icon: String = "local_police",
    override val wide: Boolean,
) : DashboardComponent {

    override val type: String = javaClass.simpleName

}