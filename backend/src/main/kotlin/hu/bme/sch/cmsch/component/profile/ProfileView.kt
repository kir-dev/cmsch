package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.debt.DebtDto
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardEntry
import hu.bme.sch.cmsch.component.token.TokenDto
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType

data class ProfileView(
    @field:JsonView(FullDetails::class)
    val loggedIn: Boolean = false,

    @field:JsonView(FullDetails::class)
    val fullName: String? = null,

    @field:JsonView(FullDetails::class)
    val groupName: String? = null,

    @field:JsonView(FullDetails::class)
    val role: RoleType = RoleType.GUEST,

    @field:JsonView(FullDetails::class)
    val alias: String? = null,

    @field:JsonView(FullDetails::class)
    val guild: GuildType? = null,

    @field:JsonView(FullDetails::class)
    val email: String? = null,

    @field:JsonView(FullDetails::class)
    val neptun: String? = null,

    @field:JsonView(FullDetails::class)
    val cmschId: String? = null,

    @field:JsonView(FullDetails::class)
    val major: MajorType? = null,

    @field:JsonView(FullDetails::class)
    val groupLeaders: List<GroupLeaderDto>? = null,

    @field:JsonView(FullDetails::class)
    val groupSelectionAllowed: Boolean = false,

    @field:JsonView(FullDetails::class)
    val availableGroups: Map<Int, String>? = null,

    @field:JsonView(FullDetails::class)
    val fallbackGroup: Int? = null,

    @field:JsonView(FullDetails::class)
    val tokens: List<TokenDto>? = null,

    @field:JsonView(FullDetails::class)
    val collectedTokenCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val totalTokenCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val minTokenToComplete: Int? = null,

    @field:JsonView(FullDetails::class)
    val totalRiddleCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val completedRiddleCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val totalTaskCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val submittedTaskCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val completedTaskCount: Int? = null,

    @field:JsonView(FullDetails::class)
    val profileIsComplete: Boolean? = null,

    @field:JsonView(FullDetails::class)
    val incompleteTasks: List<String>? = null,

    @field:JsonView(FullDetails::class)
    val locations: List<GroupMemberLocationDto>? = null,

    @field:JsonView(FullDetails::class)
    val debts: List<DebtDto>? = null,

    @field:JsonView(FullDetails::class)
    val leaderboard: List<LeaderBoardEntry>? = null,

    @field:JsonView(FullDetails::class)
    val groupMessage: String? = null,

    @field:JsonView(FullDetails::class)
    val userMessage: String? = null,
)
