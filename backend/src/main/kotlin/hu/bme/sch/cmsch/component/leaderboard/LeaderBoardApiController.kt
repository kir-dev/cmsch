package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardApiController(
    private val leaderBoardService: LeaderBoardService,
    private val leaderBoardComponent: LeaderBoardComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/leaderboard")
    fun leaderboard(auth: Authentication?): ResponseEntity<LeaderBoardView> {
        val user = auth?.getUserOrNull()

        if (!leaderBoardComponent.leaderboardEnabled)
            return ResponseEntity.ok(LeaderBoardView())

        if (!leaderBoardComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val userScore = user?.let { fetchUserScore(it) }
        val groupScore = user?.groupId?.let { fetchGroupScore(user.groupName) }

        return ResponseEntity.ok(LeaderBoardView(
            userScore = userScore,
            userBoard = if (leaderBoardComponent.showUserBoard) fetchUserBoard() else null,
            groupScore = groupScore,
            groupBoard = if (leaderBoardComponent.showGroupBoard) fetchGroupBoard() else null
        ))
    }

    @GetMapping("/detailed-leaderboard")
    fun detailedLeaderboard(auth: Authentication?): ResponseEntity<DetailedLeaderBoardView> {
        val user = auth?.getUserOrNull()

        if (!leaderBoardComponent.leaderboardDetailsEnabled)
            return ResponseEntity.ok(DetailedLeaderBoardView())

        if (!leaderBoardComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(DetailedLeaderBoardView(
            user?.id,
            leaderBoardService.getBoardDetailsForUsers(),
            user?.groupId,
            leaderBoardService.getBoardDetailsForGroups()
        ))
    }

    @GetMapping("/detailed-leaderboard-by-category")
    fun detailedLeaderboardByCategory(auth: Authentication?): ResponseEntity<DetailedCategoryLeaderBoardView> {
        val user = auth?.getUserOrNull()

        if (!leaderBoardComponent.leaderboardDetailsByCategoryEnabled)
            return ResponseEntity.ok(DetailedCategoryLeaderBoardView())

        if (!leaderBoardComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return ResponseEntity.ok(DetailedCategoryLeaderBoardView(
            user?.id,
            leaderBoardService.getBoardDetailsByCategoryForUsers(),
            user?.groupId,
            leaderBoardService.getBoardDetailsByCategoryForGroups()
        ))
    }

    private fun fetchUserScore(user: CmschUser): Int? {
        if (!leaderBoardComponent.showUserBoard)
            return null
        return leaderBoardService.getScoreOfUser(user)
    }

    private fun fetchUserBoard(): List<LeaderBoardEntryDto> {
        var limit = leaderBoardComponent.maxUserEntryToShow.toInt()
        if (limit < 0)
            limit = Int.MAX_VALUE
        val minScore = leaderBoardComponent.minScoreToShow.toInt()

        val showGroupName = leaderBoardComponent.showGroupOfUser
        if (leaderBoardComponent.showScores) {
            return leaderBoardService.getBoardForUsers()
                .take(limit)
                .filter { it.totalScore >= minScore }
                .map {
                    LeaderBoardEntryDto(
                        it.name,
                        if (showGroupName) it.groupName else "",
                        it.totalScore
                    )
                }
        }

        return leaderBoardService.getBoardForUsers()
            .take(limit)
            .filter { it.totalScore >= minScore }
            .map {
                LeaderBoardEntryDto(
                    it.name,
                    if (showGroupName) it.groupName else "",
                    null
                )
            }
    }

    private fun fetchGroupScore(groupName: String): Int? {
        if (!leaderBoardComponent.showGroupBoard)
            return null
        return leaderBoardService.getScoreOfGroup(groupName)
    }

    private fun fetchGroupBoard(): List<LeaderBoardEntryDto> {
        var limit = leaderBoardComponent.maxGroupEntryToShow.toInt()
        if (limit < 0)
            limit = Int.MAX_VALUE
        val minScore = leaderBoardComponent.minScoreToShow.toInt()

        if (leaderBoardComponent.showScores)
            return leaderBoardService.getBoardForGroups()
                .take(limit)
                .filter { it.totalScore >= minScore }
                .map { LeaderBoardEntryDto(it.name, null, it.totalScore) }

        return leaderBoardService.getBoardForGroups()
            .take(limit)
            .filter { it.totalScore >= minScore }
            .map { LeaderBoardEntryDto(it.name, null, null) }
    }

}
