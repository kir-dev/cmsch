package hu.bme.sch.cmsch.component.login.authsch

data class ActiveMembership(
    val id: Long = 0,
    val name: String = "",
    val title: List<String> = listOf()
)
