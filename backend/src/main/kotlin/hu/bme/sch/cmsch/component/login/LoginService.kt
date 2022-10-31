package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.SettingProxy
import hu.bme.sch.cmsch.component.login.authsch.ProfileResponse
import hu.bme.sch.cmsch.component.login.google.GoogleUserInfoResponse
import hu.bme.sch.cmsch.config.AUTHSCH
import hu.bme.sch.cmsch.config.GOOGLE
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
open class LoginService(
    private val users: UserService,
    private val profileService: UserProfileGeneratorService,
    private val groupToUserMapping: GroupToUserMappingRepository,
    private val guildToUserMapping: GuildToUserMappingRepository,
    private val groups: GroupRepository,
    private val loginComponent: LoginComponent,
    private val unitScopeComponent: UnitScopeComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun fetchUserEntity(profile: ProfileResponse): UserEntity {
        val user: UserEntity
        if (users.exists(profile.internalId)) {
            user = users.getById(profile.internalId)
            log.info("Logging in with existing user ${user.fullName} as authsch user")
        } else {
            user = UserEntity(
                0,
                profile.internalId,
                profile.neptun ?: "N/A",
                "",
                (profile.surname ?: "") + " " + (profile.givenName ?: ""),
                "",
                profile.email ?: "",
                RoleType.BASIC,
                groupName = "", group = null,
                guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN,
                provider = AUTHSCH
            )
            log.info("Logging in with new user ${user.fullName} internalId: ${user.internalId} as authsch user")
        }
        updateFieldsForAuthsch(user, profile)
        users.save(user)
        return user
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun fetchGoogleUserEntity(profile: GoogleUserInfoResponse): UserEntity {
        val user: UserEntity
        if (users.exists(profile.internalId)) {
            user = users.getById(profile.internalId)
            log.info("Logging in with existing user ${user.fullName} as google user")
        } else {
            user = UserEntity(
                0,
                profile.internalId,
                "N/A",
                "",
                "${profile.familyName} ${profile.givenName}",
                "",
                profile.email,
                RoleType.BASIC,
                groupName = "", group = null,
                guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN,
                provider = GOOGLE,
                profilePicture = profile.picture
            )
            log.info("Logging in with new user ${user.fullName} internalId: ${user.internalId} as google user")
        }
        updateFieldsForGoogle(user)
        users.save(user)
        return user
    }

    private fun updateFieldsForGoogle(user: UserEntity) {
        // Generate CMSch id if not present
        if (user.cmschId.isBlank()) {
            if (startupPropertyConfig.profileQrEnabled) {
                profileService.generateFullProfileForUser(user)
            } else {
                profileService.generateProfileIdForUser(user)
            }
        }

        if (loginComponent.googleAdminAddresses.getValue().split(Regex(", *")).contains(user.email)) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
        }
    }

    private fun updateFieldsForAuthsch(user: UserEntity, profile: ProfileResponse) {
        // Generate CMSch id if not present
        if (user.cmschId.isBlank()) {
            if (startupPropertyConfig.profileQrEnabled) {
                profileService.generateFullProfileForUser(user)
            } else {
                profileService.generateProfileIdForUser(user)
            }
        }

        // Update user profile values
        if (profile.neptun != null)
            user.neptun = profile.neptun ?: user.neptun
        if (profile.email != null && profile.email?.isNotBlank() == true)
            user.email = profile.email ?: user.email

        // Check neptun; grant group and guild if mapping present
        if (user.neptun.isNotBlank() && user.groupName.isBlank()) {
            groupToUserMapping.findByNeptun(user.neptun).ifPresent {
                user.major = it.major
                user.groupName = it.groupName
                user.group = groups.findByName(it.groupName).orElse(null)
            }
        }
        if (user.neptun.isNotBlank() && user.guild == GuildType.UNKNOWN) {
            guildToUserMapping.findByNeptun(user.neptun).ifPresent {
                user.guild = it.guild
            }
        }

        // Assign groups and roles
        if (profile.eduPersonEntitlement != null && user.role.value < RoleType.ADMIN.value) {
            assignStaffRole(profile, user)
            assignAdminRole(profile, user)
            assignOrganizerGroup(profile, user)
        }

        // Override superuser roles
        if (startupPropertyConfig.sysadmins.split(",").contains(user.internalId)) {
            log.info("Granting SUPERUSER for ${user.fullName}")
            user.role = RoleType.SUPERUSER
        }

        // Assign using unit-scope
        println(profile.bmeunitscope)
        val bmeUnitScopes = profile.bmeunitscope
        if (unitScopeComponent.unitScopeGrantsEnabled.isValueTrue() && bmeUnitScopes != null) {
            if (bmeUnitScopes.any { it.bme }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.bmeGrantRoleAttendee,
                    unitScopeComponent.bmeGrantRolePrivileged,
                    unitScopeComponent.bmeGrantGroupName)
            }
            if (bmeUnitScopes.any { it.active }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.activeGrantRoleAttendee,
                    unitScopeComponent.activeGrantRolePrivileged,
                    unitScopeComponent.activeGrantGroupName)
            }
            if (bmeUnitScopes.any { it.newbie }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.newbieGrantRoleAttendee,
                    unitScopeComponent.newbieGrantRolePrivileged,
                    unitScopeComponent.newbieGrantGroupName)
            }
            if (bmeUnitScopes.any { it.vik }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.vikGrantRoleAttendee,
                    unitScopeComponent.vikGrantRolePrivileged,
                    unitScopeComponent.vikGrantGroupName)
            }
            if (bmeUnitScopes.any { it.vik && it.newbie }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.vikNewbieGrantRoleAttendee,
                    unitScopeComponent.vikNewbieGrantRolePrivileged,
                    unitScopeComponent.vikNewbieGrantGroupName)
            }
            if (bmeUnitScopes.any { it.vbk }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.vbkGrantRoleAttendee,
                    unitScopeComponent.vbkGrantRolePrivileged,
                    unitScopeComponent.vbkGrantGroupName)
            }
            if (bmeUnitScopes.any { it.vbk && it.newbie }) {
                processUnitScopeStatus(user,
                    unitScopeComponent.vbkNewbieGrantRoleAttendee,
                    unitScopeComponent.vbkNewbieGrantRolePrivileged,
                    unitScopeComponent.vbkNewbieGrantGroupName)
            }
        }
        if (bmeUnitScopes != null) {
            user.unitScopes = bmeUnitScopes.joinToString(", ")
        }

        // Assign fallback group if user still don't have one
        if (user.groupName.isBlank()) {
            groups.findByName(loginComponent.fallbackGroupName.getValue()).ifPresent {
                user.groupName = it.name
                user.group = it
            }
        }

        // If fallback is still not found, set the group name to empty string
        if (user.groupName != user.group?.name)
            user.groupName = user.group?.name ?: ""
    }

    private fun processUnitScopeStatus(
        user: UserEntity,
        grantRoleAttendee: SettingProxy,
        grantRolePrivileged: SettingProxy,
        grantGroupName: SettingProxy
    ) {
        if (grantRoleAttendee.isValueTrue() && user.role < RoleType.STAFF)
            user.role = RoleType.ATTENDEE
        if (grantRolePrivileged.isValueTrue() && user.role < RoleType.STAFF)
            user.role = RoleType.PRIVILEGED
        if (grantGroupName.getValue().isNotBlank() && user.group?.leaveable != false) {
            groups.findByName(grantGroupName.getValue()).ifPresent {
                user.groupName = it.name
                user.group = it
            }
        }
    }

    private fun assignStaffRole(
        profile: ProfileResponse,
        user: UserEntity
    ) {
        val staffGroups = loginComponent.staffGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (staffGroups.isEmpty())
            return

        val grantStaffRole = profile.eduPersonEntitlement
            ?.filter { it.end == null }
            ?.any { staffGroups.contains(it.id) }
            ?: false

        if (grantStaffRole) {
            log.info("Granting STAFF for ${user.fullName}")
            user.role = RoleType.STAFF

            if (user.groupName.isBlank()) {
                groups.findByName(loginComponent.staffGroupName.getValue()).ifPresent {
                    user.groupName = it.name
                    user.group = it
                }
            }
        }
    }

    private fun assignAdminRole(
        profile: ProfileResponse,
        user: UserEntity
    ) {
        val adminGroups = loginComponent.adminGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (adminGroups.isEmpty())
            return

        val grantAdminRole = profile.eduPersonEntitlement
            ?.filter { it.end == null }
            ?.any { adminGroups.contains(it.id) }
            ?: false

        if (grantAdminRole) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
        }
    }

    private fun assignOrganizerGroup(
        profile: ProfileResponse,
        user: UserEntity
    ) {
        val organizerGroups = loginComponent.organizerGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (organizerGroups.isEmpty())
            return

        val memberOfAnyOrganizerGroups = profile.eduPersonEntitlement
            ?.filter { it.end == null }
            ?.any { organizerGroups.contains(it.id) }
            ?: false

        if (memberOfAnyOrganizerGroups && user.role.value < RoleType.STAFF.value) {
            groups.findByName(loginComponent.organizerGroupName.getValue()).ifPresent {
                log.info("Identified organizer: ${user.fullName}")
                user.groupName = it.name
                user.group = it
            }
        }
    }

}
