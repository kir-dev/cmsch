package hu.bme.sch.cmsch.component.leaderboard

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

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
    fun profile(auth: Authentication?): ResponseEntity<LeaderBoardView> {
        val user = auth?.getUserFromDatabaseOrNull()

        if (!leaderBoardComponent.leaderboardEnabled.isValueTrue())
            return ResponseEntity.ok(LeaderBoardView())

        if (!leaderBoardComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val userScore = user?.let { fetchUserScore(it) }
        val groupScore = user?.group?.let { fetchGroupScore(it) }
        return ResponseEntity.ok(LeaderBoardView(
            userScore = userScore,
            userBoard = if (leaderBoardComponent.showUserBoard.isValueTrue()) fetchUserBoard() else null,
            groupScore = groupScore,
            groupBoard = if (leaderBoardComponent.showGroupBoard.isValueTrue()) fetchGroupBoard() else null
        ))
    }

    private fun fetchUserScore(user: CmschUser): Int? {
        if (!leaderBoardComponent.showUserBoard.isValueTrue())
            return null
        return leaderBoardService.getScoreOfUser(user)
    }

    private fun fetchUserBoard(): List<LeaderBoardEntryDto> {
        val limit = leaderBoardComponent.maxUserEntryToShow.getIntValue(0)
        if (leaderBoardComponent.showScores.isValueTrue())
            return leaderBoardService.getBoardForUsers()
                .take(limit)
                .map { LeaderBoardEntryDto(it.name, it.totalScore) }

        return leaderBoardService.getBoardForUsers()
            .take(limit)
            .map { LeaderBoardEntryDto(it.name, null) }
    }

    private fun fetchGroupScore(group: GroupEntity): Int? {
        if (!leaderBoardComponent.showGroupBoard.isValueTrue())
            return null
        return leaderBoardService.getScoreOfGroup(group)
    }

    private fun fetchGroupBoard(): List<LeaderBoardEntryDto> {
        val limit = leaderBoardComponent.maxGroupEntryToShow.getIntValue(0)
        if (leaderBoardComponent.showScores.isValueTrue())
            return leaderBoardService.getBoardForGroups()
                .take(limit)
                .map { LeaderBoardEntryDto(it.name, it.totalScore) }

        return leaderBoardService.getBoardForGroups()
            .take(limit)
            .map { LeaderBoardEntryDto(it.name, null) }
    }

}
