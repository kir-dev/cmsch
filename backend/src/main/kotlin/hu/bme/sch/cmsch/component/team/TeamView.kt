package hu.bme.sch.cmsch.component.team

data class TeamView(
    var id: Int = 0,
    var name: String = "",
    var points: Int? = null,
    var members: List<TeamMemberView>? = null,
    var applicants: List<TeamMemberView>? = null,
    var joinEnabled: Boolean = false,
    var leaveEnabled: Boolean = false
)

data class TeamMemberView(
    var name: String = "",
    var id: Int = 0,
    var isAdmin: Boolean = false
)

data class TeamListView(
    var id: Int = 0,
    var name: String = ""
)
