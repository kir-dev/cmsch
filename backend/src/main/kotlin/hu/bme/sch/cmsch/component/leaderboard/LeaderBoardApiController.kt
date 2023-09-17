package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardApiController(
    private val leaderBoardService: LeaderBoardService,
    private val leaderBoardComponent: LeaderBoardComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/leaderboard")
    fun leaderboard(auth: Authentication?): ResponseEntity<LeaderBoardView> {
        val user = auth?.getUserOrNull()

        if (!leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return ResponseEntity.ok(LeaderBoardView())

        if (!leaderBoardComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val userScore = user?.let { fetchUserScore(it) }
        val groupScore = user?.groupId?.let { fetchGroupScore(user.groupName) }

        return ResponseEntity.ok(LeaderBoardView(
            userScore = userScore,
            userBoard = if (leaderBoardComponent.showUserBoard.isValueTrue()) fetchUserBoard() else null,
            groupScore = groupScore,
            groupBoard = if (leaderBoardComponent.showGroupBoard.isValueTrue()) fetchGroupBoard() else null
        ))
    }

    @GetMapping("/detailed-leaderboard")
    fun detailedLeaderboard(auth: Authentication?): ResponseEntity<DetailedLeaderBoardView> {
        val user = auth?.getUserOrNull()

        if (!leaderBoardComponent.leaderboardDetailsEnabled.isValueTrue())
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

        if (!leaderBoardComponent.leaderboardDetailsByCategoryEnabled.isValueTrue())
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
        if (!leaderBoardComponent.showUserBoard.isValueTrue())
            return null
        return leaderBoardService.getScoreOfUser(user)
    }

    private fun fetchUserBoard(): List<LeaderBoardEntryDto> {
        var limit = leaderBoardComponent.maxUserEntryToShow.getIntValue(0)
        if (limit < 0)
            limit = Int.MAX_VALUE
        val minScore = leaderBoardComponent.minScoreToShow.getIntValue(0)

        val showGroupName = leaderBoardComponent.showGroupOfUser.isValueTrue()
        if (leaderBoardComponent.showScores.isValueTrue()) {
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
        if (!leaderBoardComponent.showGroupBoard.isValueTrue())
            return null
        return leaderBoardService.getScoreOfGroup(groupName)
    }

    private fun fetchGroupBoard(): List<LeaderBoardEntryDto> {
        var limit = leaderBoardComponent.maxGroupEntryToShow.getIntValue(0)
        if (limit < 0)
            limit = Int.MAX_VALUE
        val minScore = leaderBoardComponent.minScoreToShow.getIntValue(0)

        if (leaderBoardComponent.showScores.isValueTrue())
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
