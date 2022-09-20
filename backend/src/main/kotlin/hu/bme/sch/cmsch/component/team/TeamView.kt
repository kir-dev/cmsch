package hu.bme.sch.cmsch.component.team

data class TeamStatView(
    var name: String = "",
    var value1: String = "",
    var value2: String? = null,
    var navigate: String? = null,
)

data class TeamView(
    var id: Int = 0,
    var name: String = "",
    var points: Int? = null,
    var members: List<TeamMemberView>? = null,
    var applicants: List<TeamMemberView>? = null,
    var joinEnabled: Boolean = false,
    var leaveEnabled: Boolean = false,
    var joinCancellable: Boolean = false,
    var ownTeam: Boolean = false,
    var stats: List<TeamStatView> = listOf()
)

data class TeamMemberView(
    var name: String = "",
    var id: Int = 0,
    var isAdmin: Boolean = false,
    var isYou: Boolean = false
)

data class TeamListView(
    var id: Int = 0,
    var name: String = ""
)
