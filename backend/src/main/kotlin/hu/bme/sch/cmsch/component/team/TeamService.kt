package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(TeamComponent::class)
open class TeamService(
    private val teamComponent: TeamComponent,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val teamJoinRequestRepository: TeamJoinRequestRepository,
    private val leaderBoardService: Optional<LeaderBoardService>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun createTeam(user: UserEntity, name: String): TeamCreationStatus {
        if (user.group?.leaveable == false)
            return TeamCreationStatus.ALREADY_IN_GROUP

        if (!teamComponent.creationEnabled.isValueTrue())
            return TeamCreationStatus.CREATION_DISABLED

        val finalName = name.trim()
        if (finalName.length > 64 || !finalName.matches(Regex(teamComponent.nameRegex.getValue())))
            return TeamCreationStatus.INVALID_NAME

        if (teamComponent.nameBlocklist.getValue().lowercase().split(Regex(", *")).contains(finalName.lowercase())) {
            log.info("Failed to create group with denied name: '{}' by user '{}'", finalName, user.fullName)
            return TeamCreationStatus.USED_NAME
        }

        if (groupRepository.findByNameIgnoreCase(finalName).isPresent) {
            log.info("Failed to create group with denied name: '{}' by user '{}'", finalName, user.fullName)
            return TeamCreationStatus.USED_NAME
        }

        val groupEntity = GroupEntity(
            0, finalName, MajorType.UNKNOWN,
            "${user.fullName}||", "", "", "",
            "", listOf(),
            teamComponent.racesByDefault.isValueTrue(),
            teamComponent.selectableByDefault.isValueTrue(),
            leaveable = false,
            manuallyCreated = true,
            description = "",
            profileTopMessage = ""
        )

        groupRepository.save(groupEntity)
        return userRepository.findByInternalId(user.internalId).map {
            it.groupName = groupEntity.name
            it.group = groupEntity
            if (teamComponent.grantPrivilegedRole.isValueTrue() && it.role.value < RoleType.PRIVILEGED.value) {
                log.info("Group '{}' #{} successfully created by user '{}' (PRIVILEGED granted)", finalName, groupEntity.id, user.fullName)
                it.role = RoleType.PRIVILEGED
            } else {
                log.info("Group '{}' #{} successfully created by user '{}' (PRIVILEGED not granted)", finalName, groupEntity.id, user.fullName)
            }
            userRepository.save(it)
            TeamCreationStatus.OK
        }.orElseGet {
            log.error("Group '{}' #{} successfully created by user '{}' (but user not found)", finalName, groupEntity.id, user.fullName)
            TeamCreationStatus.INTERNAL_ERROR
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun joinTeam(user: UserEntity, groupId: Int): TeamJoinStatus {
        if (user.group?.leaveable == false)
            return TeamJoinStatus.ALREADY_IN_GROUP

        if (!teamComponent.joinEnabled.isValueTrue())
            return TeamJoinStatus.JOINING_DISABLED

        val group = groupRepository.findById(groupId)
        if (group.isEmpty)
            return TeamJoinStatus.GROUP_NOT_FOUND

        if (!group.get().selectable)
            return TeamJoinStatus.NOT_JOINABLE

        if (teamJoinRequestRepository.existsByUserId(user.id))
            return TeamJoinStatus.ALREADY_SUBMITTED_JOIN_REQUEST

        teamJoinRequestRepository.save(TeamJoinRequestEntity(0, user.fullNameWithAlias, user.id, group.get().name, groupId))
        log.info("Join request recorded for user '{}' to group '{}'", user.fullName, group.get().name)
        return TeamJoinStatus.OK
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun cancelJoin(user: UserEntity): TeamJoinStatus {
        if (!teamComponent.joinEnabled.isValueTrue())
            return TeamJoinStatus.JOINING_DISABLED

        teamJoinRequestRepository.deleteAllByUserId(user.id)
        log.info("Join request cancelled for user '{}'", user.fullName)
        return TeamJoinStatus.OK
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun leaveTeam(user: UserEntity): TeamLeaveStatus {
        if (!teamComponent.leaveEnabled.isValueTrue())
            return TeamLeaveStatus.LEAVING_DISABLED

        if (user.role == RoleType.PRIVILEGED)
            return TeamLeaveStatus.LEADERS_CANNOT_LEAVE

        user.groupName = ""
        user.group = null
        userRepository.save(user)
        log.info("User '{}' left their group", user.fullName)
        return TeamLeaveStatus.OK
    }

    @Transactional(readOnly = true)
    open fun listAllTeams(): List<TeamListView>? {
        var teams = groupRepository.findAll()
            .filter { showThisTeam(it) }
            .map { TeamListView(it.id, it.name) }

        if (teamComponent.sortByName.isValueTrue())
            teams = teams.sortedBy { it.name }

        return teams
    }

    private fun showThisTeam(team: GroupEntity): Boolean {
        if (!teamComponent.showTeamsAtAll.isValueTrue())
            return false
        if (!teamComponent.showNotRacingTeams.isValueTrue() && !team.races)
            return false
        if (!teamComponent.showNotManualTeams.isValueTrue() && team.manuallyCreated != true)
            return false
        return true
    }

    @Transactional(readOnly = true)
    open fun showTeam(teamId: Int, user: UserEntity?): TeamView? {
        val team = groupRepository.findById(teamId)
        if (team.isEmpty)
            return null
        return if (showThisTeam(team.orElseThrow())) mapTeam(team.orElseThrow(), user, user?.group?.id == teamId) else null
    }

    private fun mapTeam(team: GroupEntity, user: UserEntity?, forceShowMembers: Boolean): TeamView {
        val joinCancellable = user != null && teamJoinRequestRepository.existsByUserIdAndGroupId(user.id, team.id)
        val joinEnabled = user != null
                && teamComponent.joinEnabled.isValueTrue()
                && (user.group?.leaveable ?: true)
                && user.role.value < RoleType.PRIVILEGED.value
                && !joinCancellable
        val leaveEnabled = user != null
                && teamComponent.leaveEnabled.isValueTrue()
                && user.group?.id == team.id
                && user.role.value < RoleType.PRIVILEGED.value
        val score = leaderBoardService.map { it.getScoreOfGroup(team) }.orElse(null)
        val members = if (teamComponent.showTeamMembersPublicly.isValueTrue() || forceShowMembers) mapMembers(team, user?.id) else null
        val requests = if (user != null && user.role.value >= RoleType.PRIVILEGED.value) mapRequests(team) else null
        val ownTeam = user?.group?.id == team.id
        return TeamView(team.id, team.name, score,
            members,
            requests,
            joinEnabled,
            leaveEnabled,
            joinCancellable,
            ownTeam
        )
    }

    private fun mapMembers(team: GroupEntity, userId: Int?): List<TeamMemberView> {
        return userRepository.findAllByGroupName(team.name)
            .map { TeamMemberView(it.fullNameWithAlias, it.id, it.role.value >= RoleType.PRIVILEGED.value, it.id == userId) }
    }

    private fun mapRequests(team: GroupEntity): List<TeamMemberView> {
        return teamJoinRequestRepository.findAllByGroupId(team.id)
            .map { TeamMemberView(it.userName, it.userId, isAdmin = false, isYou = false) }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun acceptJoin(userId: Int, group: GroupEntity?): Boolean {
        if (group == null)
            return false

        if (teamJoinRequestRepository.existsByUserIdAndGroupId(userId, group.id)) {
            val user = userRepository.findById(userId).orElse(null) ?: return false
            user.groupName = group.name
            user.group = group
            if (teamComponent.grantAttendeeRole.isValueTrue() && user.role.value <= RoleType.ATTENDEE.value) {
                log.info("User '{}' accepted for group '{}' (ATTENDEE granted)", user.fullName, group.name)
                user.role = RoleType.ATTENDEE
            } else {
                log.info("User '{}' accepted for group '{}' (ATTENDEE not granted)", user.fullName, group.name)
            }
            userRepository.save(user)
            teamJoinRequestRepository.deleteAllByUserId(userId)
            return true
        }
        return false
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun rejectJoin(userId: Int, group: GroupEntity?): Boolean {
        if (group == null)
            return false

        if (teamJoinRequestRepository.existsByUserIdAndGroupId(userId, group.id)) {
            val user = userRepository.findById(userId).orElse(null) ?: return false
            log.info("User '{}' rejected for group '{}'", user.fullName, group.name)
            teamJoinRequestRepository.deleteAllByUserId(userId)
            return true
        }
        return false
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun toggleUserPermissions(userId: Int, group: GroupEntity?, adminUser: CmschUser): Boolean {
        if (group == null)
            return false

        if (userId == adminUser.id) {
            log.info("Failed to switch user permissions '{}' from '{}' by '{}', reason: can't revoke your permissions",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        if (!teamComponent.togglePermissionEnabled.isValueTrue()) {
            log.info("Failed to switch user permissions '{}' from '{}' by '{}', reason: disabled by config",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        val user = userRepository.findById(userId).orElse(null)
            ?: return false

        if (user.group?.id != group.id) {
            log.info("Failed to switch user permissions '{}' from '{}', reason: groups does not match",
                user.fullName, group.name)
            return false
        }

        return if (user.role.value >= RoleType.STAFF.value) {
            log.info("User '{}' is at least staff, cannot be revoked", user.fullName)
            true
        } else if (user.role.value < RoleType.PRIVILEGED.value) {
            user.role = RoleType.PRIVILEGED
            userRepository.save(user)
            log.info("User '{}' is now group leader at '{}' by '{}'", user.fullName, group.name, adminUser.userName)
            true
        } else {
            user.role = if (teamComponent.grantPrivilegedRole.isValueTrue()) RoleType.ATTENDEE else RoleType.BASIC
            userRepository.save(user)
            log.info("User '{}' is now regular group member at '{}' by '{}'", user.fullName, group.name, adminUser.userName)
            true
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun promoteLeader(userId: Int, group: GroupEntity?, adminUser: UserEntity): Boolean {
        if (group == null)
            return false

        if (userId == adminUser.id) {
            log.info("Failed to promote leadership to user '{}' from '{}' by '{}', reason: can't revoke your permissions",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        if (!teamComponent.promoteLeadershipEnabled.isValueTrue()) {
            log.info("Failed to promote leadership to user '{}' from '{}' by '{}', reason: disabled by config",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        val user = userRepository.findById(userId).orElse(null)
            ?: return false

        if (user.group?.id != group.id) {
            log.info("Failed to promote leadership to user '{}' from '{}', reason: groups does not match",
                user.fullName, group.name)
            return false
        }

        if (user.role.value < RoleType.STAFF.value)
            user.role = RoleType.PRIVILEGED
        userRepository.save(user)
        if (adminUser.role.value < RoleType.STAFF.value)
            adminUser.role = if (teamComponent.grantPrivilegedRole.isValueTrue()) RoleType.ATTENDEE else RoleType.BASIC
        userRepository.save(adminUser)
        log.info("User '{}' is now group leader at '{}' switched with '{}'", user.fullName, group.name, adminUser.userName)
        return true
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun kickUser(userId: Int, group: GroupEntity?, adminUser: CmschUser): Boolean {
        if (group == null)
            return false

        if (userId == adminUser.id) {
            log.info("Failed to kick user '{}' from '{}' by '{}', reason: can't kick yourself",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        if (!teamComponent.kickEnabled.isValueTrue()) {
            log.info("Failed to kick user '{}' from '{}' by '{}', reason: disabled by config",
                adminUser.userName, group.name, adminUser.userName)
            return false
        }

        val user = userRepository.findById(userId).orElse(null)
            ?: return false

        if (user.group?.id != group.id) {
            log.info("Failed to kick user '{}' from '{}' by '{}', reason: groups does not match",
                user.fullName, group.name, adminUser.userName)
            return false
        }
        if (user.role.value >= RoleType.PRIVILEGED.value) {
            log.info("Failed to kick user '{}' from '{}' by '{}', reason: admins cannot be kicked",
                user.fullName, group.name, adminUser.userName)
            return false
        }

        user.group = null
        user.groupName = ""
        if (user.role.value < RoleType.STAFF.value)
            user.role = RoleType.BASIC
        userRepository.save(user)
        log.info("User '{}' kicked from '{}', reason: by admin '{}'", user.fullName, group.name, adminUser.userName)
        return true
    }


}
