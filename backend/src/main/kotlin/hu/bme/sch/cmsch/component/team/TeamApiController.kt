package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(TeamComponent::class)
class TeamApiController(
    private val teamComponent: TeamComponent,
    private val teamService: TeamService

) {

    @PostMapping("/team/create")
    fun createTeam(@RequestBody teamCreationDto: TeamCreationDto, auth: Authentication?): TeamCreationStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.createMinRole.isAvailableForRole(user.role))
            return TeamCreationStatus.INSUFFICIENT_PERMISSIONS

        return teamService.createTeam(user, teamCreationDto.name)
    }

    @PostMapping("/team/join")
    fun joinTeam(@RequestBody teamJoinDto: TeamJoinDto, auth: Authentication?): TeamJoinStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.minRole.isAvailableForRole(user.role))
            return TeamJoinStatus.INSUFFICIENT_PERMISSIONS
        return teamService.joinTeam(user, teamJoinDto.id)
    }

    @PostMapping("/team/edit")
    fun setDescriptionAndLogo(
        @RequestParam description: String,
        @RequestParam logo: MultipartFile?,
        auth: Authentication?,
    ): TeamEditStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || user.role.value < RoleType.PRIVILEGED.value)
            throw IllegalStateException("Insufficient permissions")

        return teamService.setDescriptionAndLogo(description, logo, user)
    }

    @PostMapping("/team/cancel-join")
    fun cancelJoinTeam(auth: Authentication?): TeamJoinStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.minRole.isAvailableForRole(user.role))
            return TeamJoinStatus.INSUFFICIENT_PERMISSIONS
        return teamService.cancelJoin(user)
    }

    @PostMapping("/team/leave")
    fun leaveTeam(auth: Authentication?): TeamLeaveStatus {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.minRole.isAvailableForRole(user.role))
            return TeamLeaveStatus.INSUFFICIENT_PERMISSIONS
        return teamService.leaveTeam(user)
    }

    @GetMapping("/team/my")
    fun showMyGroup(auth: Authentication?): ResponseEntity<TeamView> {
        val user = auth?.getUserOrNull()
        if (user == null || !teamComponent.myMinRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val groupId = user.groupId
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        if (!teamComponent.showTeamDetails.isValueTrue())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return teamService.showTeam(groupId, user)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @GetMapping("/team/{teamId}")
    fun showGroupDetails(@PathVariable teamId: Int, auth: Authentication?): ResponseEntity<TeamView> {
        val user = auth?.getUserOrNull()
        if (!teamComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        if (!teamComponent.showTeamDetails.isValueTrue())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return teamService.showTeam(teamId, user)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @GetMapping("/teams")
    fun listAllTeams(auth: Authentication?): ResponseEntity<List<TeamListView>> {
        val user = auth?.getUserOrNull()
        if (!teamComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(listOf())
        if (!teamComponent.showTeamsAtAll.isValueTrue())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return ResponseEntity.ok(teamService.listAllTeams())
    }

    data class UserIdRequest(var id: Int = 0)

    @PutMapping("/team/admin/accept-join")
    fun acceptJoin(@RequestBody request: UserIdRequest, auth: Authentication?): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.adminMinRole.isAvailableForRole(user.role))
            return false
        return teamService.acceptJoin(request.id, user.group)
    }

    @PutMapping("/team/admin/reject-join")
    fun rejectJoin(@RequestBody request: UserIdRequest, auth: Authentication?): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.adminMinRole.isAvailableForRole(user.role))
            return false
        return teamService.rejectJoin(request.id, user.groupId, user.groupName)
    }

    @PutMapping("/team/admin/toggle-permissions")
    fun togglePermissions(@RequestBody request: UserIdRequest, auth: Authentication?): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.adminMinRole.isAvailableForRole(user.role))
            return false
        return teamService.toggleUserPermissions(request.id, user.groupId, user.groupName, user)
    }

    @PutMapping("/team/admin/kick-user")
    fun kickUser(@RequestBody request: UserIdRequest, auth: Authentication?): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.adminMinRole.isAvailableForRole(user.role))
            return false
        return teamService.kickUser(request.id, user.groupId, user.groupName, user)
    }

    @PutMapping("/team/admin/switch-leadership")
    fun switchLeadership(@RequestBody request: UserIdRequest, auth: Authentication?): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull()
        if (user == null || !teamComponent.adminMinRole.isAvailableForRole(user.role))
            return false
        return teamService.promoteLeader(request.id, user.groupId, user.groupName, user)
    }

}
