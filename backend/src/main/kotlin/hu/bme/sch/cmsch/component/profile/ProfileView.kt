package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.debt.DebtDto
import hu.bme.sch.cmsch.component.leaderboard.TopListAbstractEntryDto
import hu.bme.sch.cmsch.component.token.TokenDto
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType

data class ProfileView(
    @JsonView(FullDetails::class)
    val loggedIn: Boolean = false,

    @JsonView(FullDetails::class)
    val fullName: String? = null,

    @JsonView(FullDetails::class)
    val groupName: String? = null,

    @JsonView(FullDetails::class)
    val role: RoleType = RoleType.GUEST,

    @JsonView(FullDetails::class)
    val alias: String? = null,

    @JsonView(FullDetails::class)
    val guild: GuildType? = null,

    @JsonView(FullDetails::class)
    val email: String? = null,

    @JsonView(FullDetails::class)
    val neptun: String? = null,

    @JsonView(FullDetails::class)
    val cmschId: String? = null,

    @JsonView(FullDetails::class)
    val major: MajorType? = null,

    @JsonView(FullDetails::class)
    val groupLeaders: List<GroupLeaderDto>? = null,

    @JsonView(FullDetails::class)
    val groupSelectionAllowed: Boolean = false,

    @JsonView(FullDetails::class)
    val availableGroups: Map<Int, String>? = null,

    @JsonView(FullDetails::class)
    val fallbackGroup: Int? = null,

    @JsonView(FullDetails::class)
    val tokens: List<TokenDto>? = null,

    @JsonView(FullDetails::class)
    val collectedTokenCount: Int? = null,

    @JsonView(FullDetails::class)
    val totalTokenCount: Int? = null,

    @JsonView(FullDetails::class)
    val minTokenToComplete: Int? = null,

    @JsonView(FullDetails::class)
    val totalRiddleCount: Int? = null,

    @JsonView(FullDetails::class)
    val completedRiddleCount: Int? = null,

    @JsonView(FullDetails::class)
    val totalTaskCount: Int? = null,

    @JsonView(FullDetails::class)
    val submittedTaskCount: Int? = null,

    @JsonView(FullDetails::class)
    val completedTaskCount: Int? = null,

    @JsonView(FullDetails::class)
    val locations: List<GroupMemberLocationDto>? = null,

    @JsonView(FullDetails::class)
    val debts: List<DebtDto>? = null,

    @JsonView(FullDetails::class)
    val leaderboard: List<TopListAbstractEntryDto>? = null,

)
