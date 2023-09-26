package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.SettingProxy
import hu.bme.sch.cmsch.component.login.authsch.ProfileResponse
import hu.bme.sch.cmsch.component.login.google.GoogleUserInfoResponse
import hu.bme.sch.cmsch.component.login.keycloak.KeycloakUserInfoResponse
import hu.bme.sch.cmsch.config.AUTHSCH
import hu.bme.sch.cmsch.config.GOOGLE
import hu.bme.sch.cmsch.config.KEYCLOAK
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.repository.UserDetailsByInternalIdMappingRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager

@Service
open class LoginService(
    private val users: UserService,
    private val profileService: UserProfileGeneratorService,
    private val groupToUserMapping: GroupToUserMappingRepository,
    private val userDetailsByInternalIdMapping: UserDetailsByInternalIdMappingRepository,
    private val guildToUserMapping: GuildToUserMappingRepository,
    private val groups: GroupRepository,
    private val loginComponent: LoginComponent,
    private val unitScopeComponent: UnitScopeComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val adminMenuService: AdminMenuService,
    private val transactionManager: PlatformTransactionManager
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val userLocks = InternalIdLocks()

    fun fetchUserEntity(profile: ProfileResponse): UserEntity {
        val lock = userLocks.lockForKey(profile.internalId)
        try {
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
            transactionManager.transaction(readOnly = true) { updateFieldsForAuthsch(user, profile) }
            transactionManager.transaction(readOnly = false) { users.save(user) }
            adminMenuService.invalidateUser(user.internalId)
            return user
        } finally {
            lock.unlock()
        }
    }

    fun fetchGoogleUserEntity(profile: GoogleUserInfoResponse): UserEntity {
        val lock = userLocks.lockForKey(profile.internalId)
        try {
            val user: UserEntity
            if (users.exists(profile.internalId)) {
                user = users.getById(profile.internalId)
                log.info("Logging in with existing user ${user.fullName} as google user")
            } else {
                user = UserEntity(
                    0,
                    profile.internalId.take(254),
                    "N/A",
                    "",
                    "${profile.familyName} ${profile.givenName}".take(254),
                    "",
                    profile.email.take(254),
                    RoleType.BASIC,
                    groupName = "", group = null,
                    guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN,
                    provider = GOOGLE,
                    profilePicture = profile.picture.take(254)
                )
                log.info("Logging in with new user ${user.fullName} internalId: ${user.internalId} as google user profile picture: ${profile.picture}")
            }
            transactionManager.transaction(readOnly = true) { updateFieldsForGoogle(user) }
            transactionManager.transaction(readOnly = false) { users.save(user) }
            adminMenuService.invalidateUser(user.internalId)
            return user
        } finally {
            lock.unlock()
        }
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

        // Grant admin by email
        if (loginComponent.googleAdminAddresses.getValue().split(Regex(", *")).contains(user.email)) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
            user.detailsImported = true
        }

        // Assign fallback group if user still don't have one
        if (user.groupName.isBlank()) {
            groups.findByName(loginComponent.fallbackGroupName.getValue()).ifPresent {
                user.groupName = it.name
                user.group = it
                user.detailsImported = true
            }
        }

        // If fallback is still not found, set the group name to empty string
        if (user.groupName != user.group?.name)
            user.groupName = user.group?.name ?: ""
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
        if (profile.neptun != null) {
            user.neptun = profile.neptun ?: user.neptun
        }
        if (profile.email != null && profile.email?.isNotBlank() == true) {
            user.email = profile.email ?: user.email
        }

        // Check neptun; grant group and guild if mapping present
        if (user.neptun.isNotBlank()) {
            groupToUserMapping.findByNeptun(user.neptun).ifPresent {
                user.major = it.major
                user.groupName = it.groupName
                user.group = groups.findByName(it.groupName).orElse(null)
                user.detailsImported = true
            }
        }

        if (user.internalId.isNotBlank() && !user.detailsImported) {
            userDetailsByInternalIdMapping.findByInternalId(user.internalId).ifPresent {
                if(it.allDetailsImported()) {
                    user.neptun = it.neptun!!
                    user.role = it.role!!
                    user.groupName = it.groupName!!
                    user.group = groups.findByName(it.groupName.toString()).orElse(null)
                    user.guild = it.guild!!
                    user.major = it.major!!
                    user.permissions = it.permissions!!
                    user.profilePicture = it.profilePicture!!
                    user.profileTopMessage = it.profileTopMessage!!
                }
                user.detailsImported = true
            }
        }

        if (user.neptun.isNotBlank()) {
            guildToUserMapping.findByNeptun(user.neptun).ifPresent {
                user.guild = it.guild
                user.detailsImported = true
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

    fun fetchKeycloakUserEntity(profile: KeycloakUserInfoResponse): UserEntity {
        val user: UserEntity
        if (users.exists(profile.sid)) {
            user = users.getById(profile.sid)
            log.info("Logging in with existing user ${user.fullName} as keycloak user")
        } else {
            user = UserEntity(
                    0,
                    profile.sid,
                    "N/A",
                    "",
                    "${profile.familyName} ${profile.givenName}",
                    profile.preferredUsername,
                    profile.email,
                    RoleType.BASIC,
                    groupName = "", group = null,
                    guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN,
                    provider = KEYCLOAK,
                    profilePicture = ""
            )
            log.info("Logging in with new user ${user.fullName} internalId: ${user.internalId} as keycloak user")
        }
        updateFieldsForKeycloak(profile, user)
        users.save(user)
        return user
    }

    private fun updateFieldsForKeycloak(profile: KeycloakUserInfoResponse, user: UserEntity) {
        // Generate CMSch id if not present
        if (user.cmschId.isBlank()) {
            if (startupPropertyConfig.profileQrEnabled) {
                profileService.generateFullProfileForUser(user)
            } else {
                profileService.generateProfileIdForUser(user)
            }
        }

        // Grant admin by email
        if (loginComponent.keycloakAdminAddresses.getValue().split(Regex(", *")).contains(user.email)) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
            user.detailsImported = true
        }

        // Grant roles by keycloak role names
        if (profile.groups.contains(loginComponent.keycloakStaffRole.getValue())) {
            log.info("Granting STAFF for ${user.fullName}")
            user.role = RoleType.STAFF
            user.detailsImported = true
        }

        if (profile.groups.contains(loginComponent.keycloakAdminRole.getValue())) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
            user.detailsImported = true
        }

        if (profile.groups.contains(loginComponent.keycloakSuperuserRole.getValue())) {
            log.info("Granting SUPERUSER for ${user.fullName}")
            user.role = RoleType.SUPERUSER
            user.detailsImported = true
        }

        // Assign fallback group if user still don't have one
        if (user.groupName.isBlank()) {
            groups.findByName(loginComponent.fallbackGroupName.getValue()).ifPresent {
                user.groupName = it.name
                user.group = it
                user.detailsImported = true
            }
        }

        // If fallback is still not found, set the group name to empty string
        if (user.groupName != user.group?.name)
            user.groupName = user.group?.name ?: ""
    }

}
