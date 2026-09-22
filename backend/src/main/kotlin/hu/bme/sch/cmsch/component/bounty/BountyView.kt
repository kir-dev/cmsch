package hu.bme.sch.cmsch.component.bounty

enum class BountyPhase {
    BEFORE_REGISTRATION, REGISTRATION, BEFORE_GAME, ACTIVE, FINISHED
}

data class BountyView(
    val rounds: List<BountyRoundView>,
    val myRegistrationQr: String? = null,
    val myGroupId: Int? = null,
)

data class BountyRoundView(
    val id: Int,
    val name: String,
    val difficulty: String,
    val registrationStart: Long,
    val registrationEnd: Long,
    val gameStart: Long,
    val gameEnd: Long,
    val phase: BountyPhase,
    val finalized: Boolean,
    val winnerGroupName: String?,
    val myRegistration: BountyRegistrationView?,
    val myTeam: BountyTeamView?,
)

data class BountyRegistrationView(
    val alive: Boolean,
    val code: String?,
    val deadline: Long?,
    val eliminatedAt: Long?,
    val eliminatedByInactivity: Boolean,
)

data class BountyTeamView(
    val groupName: String,
    val aliveCount: Int,
    val targetGroupName: String?,
    val targetAliveCount: Int?,
    val weapon: String?,
    val winner: Boolean,
    val rank: Int?,
    val kills: Int,
)

data class BountyKillRequest(
    val code: String = "",
)

data class BountyKillResponse(
    val success: Boolean,
    val message: String,
)

data class BountyRegistrationResponse(
    val ok: Boolean,
    val message: String,
    val userName: String = "",
    val groupName: String = "",
    val roundName: String = "",
)
