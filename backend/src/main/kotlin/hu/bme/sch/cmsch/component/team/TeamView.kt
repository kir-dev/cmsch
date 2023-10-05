package hu.bme.sch.cmsch.component.team

data class TeamStatView(
    var name: String = "",
    var value1: String = "",
    var value2: String? = null,
    var navigate: String? = null,
    var percentage: Float? = null
)

data class TaskCategoryPreview(
    var name: String = "",
    var completed: Int = 0,
    var outOf: Int = 0,
    var navigate: Int? = null,
)

data class AdvertisedFormPreview(
    var name: String = "",
    var filled: Boolean = false,
    var availableUntil: Long = 0,
    var url: String = ""
)

data class TeamView(
    var id: Int = 0,
    var name: String = "",
    val coverUrl: String = "",
    val description: String = "",
    val logo: String? = null,
    var points: Int? = null,
    var members: List<TeamMemberView>? = null,
    var applicants: List<TeamMemberView>? = null,
    var joinEnabled: Boolean = false,
    var leaveEnabled: Boolean = false,
    var joinCancellable: Boolean = false,
    var ownTeam: Boolean = false,
    var stats: List<TeamStatView> = listOf(),
    var taskCategories: List<TaskCategoryPreview> = listOf(),
    var forms: List<AdvertisedFormPreview> = listOf(),
    var leaderNotes: String? = null
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
