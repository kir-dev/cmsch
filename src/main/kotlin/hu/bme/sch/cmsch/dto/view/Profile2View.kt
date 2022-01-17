package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity

data class Profile2View(
        @JsonView(FullDetails::class)
        val fullName: String = "",

        @JsonView(FullDetails::class)
        val groupName: String? = null,

        @JsonView(FullDetails::class)
        val role: RoleType = RoleType.GUEST,

        @JsonView(FullDetails::class)
        val tokens: List<TokenDto> = listOf(),

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
        val minTokenToComplete: Int = 0,
)
