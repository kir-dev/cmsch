package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.token.SESSION_TOKEN_COLLECTOR_ATTRIBUTE
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.service.JwtTokenProvider
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.sha256
import hu.gerviba.authsch.AuthSchAPI
import hu.gerviba.authsch.response.ProfileDataResponse
import io.swagger.annotations.ApiOperation
import org.apache.catalina.util.URLEncoder
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@ConditionalOnBean(LoginComponent::class)
class LoginController(
    private val authSch: AuthSchAPI,
    private val users: UserService,
    private val profileService: UserProfileGeneratorService,
    private val groupToUserMapping: GroupToUserMappingRepository,
    private val guildToUserMapping: GuildToUserMappingRepository,
    private val groups: GroupRepository,
    private val applicationComponent: ApplicationComponent,
    private val loginComponent: LoginComponent,
    private val jwtTokenProvider: JwtTokenProvider,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val encoder = URLEncoder()

    @ResponseBody
    @GetMapping("/control/logged-out")
    fun loggedOut(): String {
        return "Sikeres kijelentkezés!"
    }

    @ApiOperation("Login re-entry point")
    @GetMapping("/control/auth/authsch/callback")
    fun loggedIn(@RequestParam code: String,
                 @RequestParam state: String,
                 request: HttpServletRequest,
                 httpResponse: HttpServletResponse

    ) {
        if (buildUniqueState(request) != state) {
            httpResponse.sendRedirect("index?invalid-state")
            return
        }

        var auth: Authentication? = null
        try {
            val response = authSch.validateAuthentication(code)
            val profile = authSch.getProfile(response.accessToken)
            val user: UserEntity
            if (users.exists(profile.internalId.toString())) {
                user = users.getById(profile.internalId.toString())
                log.info("Logging in with existing user ${user.fullName}")
            } else {
                user = UserEntity(0,
                        profile.internalId.toString(),
                        profile.neptun ?: "N/A",
                        "",
                        (profile.surname ?: "") + " " + (profile.givenName ?: ""),
                        "",
                        profile.mail ?: "",
                        RoleType.BASIC,
                        groupName = "", group = null,
                        guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN
                )
                log.info("Logging in with new user ${user.fullName} internalId: ${user.internalId}")
            }
            updateFields(user, profile)

            auth = UsernamePasswordAuthenticationToken(
                getPrincipal(user, profile),
                getCredentials(profile, user),
                getAuthorities(user)
            )

            request.getSession(true).maxInactiveInterval = (startupPropertyConfig.sessionValidityInMilliseconds / 1000).toInt()
            SecurityContextHolder.getContext().authentication = auth
        } catch (e: Exception) {
            auth?.isAuthenticated = false
            e.printStackTrace()
        }

        if (request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE) != null) {
            httpResponse.sendRedirect("/api/token-after-login")
            return
        }
        httpResponse.sendRedirect(if (auth != null && auth.isAuthenticated) "/control/entrypoint" else "/control/logged-out?error")
    }

    private fun getCredentials(profile: ProfileDataResponse, user: UserEntity): String {
        return if (startupPropertyConfig.jwtEnabled)
            jwtTokenProvider.createToken(user.id, profile.internalId.toString(), user.role, user.permissionsAsList)
        else
            ""
    }

    private fun getPrincipal(user: UserEntity, profile: ProfileDataResponse) = CmschUserPrincipal(
        id = user.id,
        internalId = profile.internalId.toString(),
        role = user.role,
        permissions = user.permissionsAsList
    )

    private fun updateFields(user: UserEntity, profile: ProfileDataResponse) {
        // Generate CMSch id if not present
        if (user.cmschId.isBlank()) {
            if (startupPropertyConfig.profileQrEnabled) {
                profileService.generateFullProfileForUser(user)
            } else {
                profileService.generateProfileIdForUser(user)
            }
        }

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
        if (profile.eduPersonEntitlements != null && user.role == RoleType.BASIC) {
            assignStaffRole(profile, user)
            assignAdminRole(profile, user)
            assignOrganizerGroup(profile, user)
        }

        // Override superuser roles
        if (startupPropertyConfig.sysadmins.split(",").contains(user.internalId)) {
            log.info("Granting SUPERUSER for ${user.fullName}")
            user.role = RoleType.SUPERUSER
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
        users.save(user)
    }

    private fun assignStaffRole(
        profile: ProfileDataResponse,
        user: UserEntity
    ) {
        val staffGroups = loginComponent.staffGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (staffGroups.isEmpty())
            return

        val grantStaffRole = profile.eduPersonEntitlements
            .filter { it.end == null }
            .any { staffGroups.contains(it.id) }

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
        profile: ProfileDataResponse,
        user: UserEntity
    ) {
        val adminGroups = loginComponent.adminGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (adminGroups.isEmpty())
            return

        val grantAdminRole = profile.eduPersonEntitlements
            .filter { it.end == null }
            .any { adminGroups.contains(it.id) }

        if (grantAdminRole) {
            log.info("Granting ADMIN for ${user.fullName}")
            user.role = RoleType.ADMIN
        }
    }

    private fun assignOrganizerGroup(
        profile: ProfileDataResponse,
        user: UserEntity
    ) {
        val organizerGroups = loginComponent.organizerGroups.getValue()
            .split(',')
            .mapNotNull { it.toLongOrNull() }
            .toLongArray()
        if (organizerGroups.isEmpty())
            return

        val memberOfAnyOrganizerGroups = profile.eduPersonEntitlements
            .filter { it.end == null }
            .any { organizerGroups.contains(it.id) }

        if (memberOfAnyOrganizerGroups && user.role.value < RoleType.STAFF.value) {
            groups.findByName(loginComponent.organizerGroupName.getValue()).ifPresent {
                log.info("Identified organizer: ${user.fullName}")
                user.groupName = it.name
                user.group = it
            }
        }
    }

    private fun getAuthorities(user: UserEntity): List<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        return authorities
    }

    @ApiOperation("Redirection to the auth provider")
    @GetMapping("/control/login")
    fun items(request: HttpServletRequest): String {
        return "redirect:${generateLoginUrl(buildUniqueState(request))}"
    }

    fun generateLoginUrl(uniqueId: String): String {
        return if (loginComponent.onlyBmeProvider.isValueTrue()) {
            val target = "https://auth.sch.bme.hu/site/login/provider/bme?" +
                    "response_type=code" +
                    "&client_id=${authSch.clientIdentifier}" +
                    "&state=${uniqueId}" +
                    "&scope=${loginComponent.authschScopes.joinToString("+") { it.scope }}"
            "https://auth.sch.bme.hu/Shibboleth.sso/Login?target=${encoder.encode(target, StandardCharsets.UTF_8)}"
        } else {
            "${authSch.loginUrlBase}?" +
                    "response_type=code" +
                    "&client_id=${authSch.clientIdentifier}" +
                    "&state=${uniqueId}" +
                    "&scope=${loginComponent.authschScopes.joinToString("+") { it.scope }}"
        }
    }

    fun buildUniqueState(request: HttpServletRequest): String {
        return (request.session.id + request.localAddr).sha256()
    }

    @ApiOperation("Logout user")
    @GetMapping("/control/logout")
    fun logout(request: HttpServletRequest, auth: Authentication, httpResponse: HttpServletResponse): String {
        log.info("Logging out from user {}", auth.getUserOrNull()?.internalId ?: "n/a")

        try {
            SecurityContextHolder.getContext().authentication = null
            val session = request.getSession(false)
            session?.invalidate()
            request.changeSessionId()

        } catch (e: Exception) {
            // Ignore it for now
        }
        return "redirect:${applicationComponent.siteUrl.getValue()}?logged-out=true"
    }

    @GetMapping("/control/open-site")
    fun openSite(auth: Authentication, request: HttpServletRequest): String {
        if (startupPropertyConfig.jwtEnabled)
            return "redirect:${applicationComponent.siteUrl.getValue()}?jwt=${encoder.encode(auth.credentials.toString(), StandardCharsets.UTF_8)}"
        return "redirect:${applicationComponent.siteUrl.getValue()}"
    }

}
