package hu.bme.sch.cmsch.component.login.authsch

data class PersonEntitlement(
    var id: Long = 0,
    var name: String = "",
    var status: String = "",
    var title: List<String> = listOf(),
    var start: String = "",
    var end: String? = null
)
