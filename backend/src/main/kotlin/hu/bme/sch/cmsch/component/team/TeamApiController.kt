package hu.bme.sch.cmsch.component.team

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(TeamComponent::class)
class TeamApiController(
    private val teamComponent: TeamComponent
) {

    @PostMapping("/team/create")
    fun createTeam(auth: Authentication?): TeamCreationStatus {
        return TeamCreationStatus.OK
    }

    @PostMapping("/team/join")
    fun joinTeam(auth: Authentication?): TeamJoinStatus {
        return TeamJoinStatus.OK
    }

    @PostMapping("/team/cancel-join")
    fun cancelJoinTeam(auth: Authentication?): TeamJoinStatus {
        return TeamJoinStatus.OK
    }

    @PostMapping("/team/leave")
    fun leaveGroup(auth: Authentication?): TeamLeaveStatus {
        return TeamLeaveStatus.OK
    }

    @GetMapping("/team/my")
    fun showMyGroup(auth: Authentication?): ResponseEntity<TeamView> {
        return ResponseEntity.ok(TeamView(
            20,
            "Mock Csapat",
            9000,
            listOf(
                TeamMemberView("Admin User", 299, true),
                TeamMemberView("User1", 300, false),
                TeamMemberView("User2", 301, false)),
            listOf(
                TeamMemberView("User3", 302, false)
            ),
            true,
            false
        ))
    }

    @GetMapping("/team/{teamId}")
    fun showGroupDetails(teamId: Int, auth: Authentication?): ResponseEntity<TeamView> {
        return ResponseEntity.ok(TeamView(
            21,
            "Mock Csapat 2",
            5000,
            listOf(
                TeamMemberView("Admin User", 299, true),
                TeamMemberView("User1", 300, false),
                TeamMemberView("User2", 301, false)),
            listOf(
                TeamMemberView("User3", 302, false)
            ),
            true,
            false
        ))
    }

    @GetMapping("/teams")
    fun listAllGroups(): List<TeamListView> {
        return listOf(
            TeamListView(20, "Mock Csapat"),
            TeamListView(21, "Mock Csapat 2"),
            TeamListView(22, "Awesome Csapat"),
            TeamListView(23, "Gólyák xddd"),
        )
    }

    @PutMapping("/team/admin/accept-join")
    fun acceptJoin(@RequestBody userId: Int, auth: Authentication?): Boolean {
        return true
    }

    @PutMapping("/team/admin/reject-join")
    fun rejectJoin(@RequestBody userId: Int, auth: Authentication?): Boolean {
        return true
    }

}
