package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.token.TokenDto
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.RoleType

data class Profile2View(
    @JsonView(FullDetails::class)
        val fullName: String = "",

    @JsonView(FullDetails::class)
        val groupName: String? = null,

    @JsonView(FullDetails::class)
        val groupSelectionAllowed: Boolean = false,

    @JsonView(FullDetails::class)
        val availableGroups: Map<Int, String> = mapOf(),

    @JsonView(FullDetails::class)
        val fallbackGroup: Int? = null,

    @JsonView(FullDetails::class)
        val role: RoleType = RoleType.GUEST,

    @JsonView(FullDetails::class)
        val tokens: List<TokenDto> = listOf(),

    @JsonView(FullDetails::class)
        val collectedTokenCount: Int = 0,

    @JsonView(FullDetails::class)
        val totalTokenCount: Int = 0,

    @JsonView(FullDetails::class)
        val totalRiddleCount: Int = 0,

    @JsonView(FullDetails::class)
        val completedRiddleCount: Int = 0,

    @JsonView(FullDetails::class)
        val totalAchievementCount: Int = 0,

    @JsonView(FullDetails::class)
        val submittedAchievementCount: Int = 0,

    @JsonView(FullDetails::class)
        val completedAchievementCount: Int = 0,

    @JsonView(FullDetails::class)
        val minTokenToComplete: Int = 0,
)
