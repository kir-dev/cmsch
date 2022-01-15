package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.controller.api.SESSION_TOKEN_COLLECTOR_ATTRIBUTE
import hu.bme.sch.cmsch.dao.GroupRepository
import hu.bme.sch.cmsch.dao.GroupToUserMappingRepository
import hu.bme.sch.cmsch.dao.GuildToUserMappingRepository
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.MajorType
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.service.UserProfileGeneratorService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.sha256
import hu.gerviba.authsch.AuthSchAPI
import hu.gerviba.authsch.response.ProfileDataResponse
import hu.gerviba.authsch.struct.Scope
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val USER_SESSION_ATTRIBUTE_NAME = "user_id"
const val USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME = "user"
const val CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME = "circles"
const val LOGGED_IN_COOKIE = "loggedIn"
const val SESSION_TIMEOUT = 7 * 24 * 60 * 60

@Controller
open class LoginController(
        private val authSch: AuthSchAPI,
        private val users: UserService,
        private val profileService: UserProfileGeneratorService,
        private val groupToUserMapping: GroupToUserMappingRepository,
        private val guildToUserMapping: GuildToUserMappingRepository,
        private val groups: GroupRepository,
        @Value("\${cmsch.pek-group-grant-name:Szent Schönherz Senior Lovagrend}") private val grantStaffGroupName: String,
        @Value("\${cmsch.sysadmins:}") private val systemAdmins: String,
        @Value("\${cmsch.default-staff-group-name:STAFF}") private val staffGroupName: String,
        @Value("\${cmsch.default-group-name:DEFAULT}") private val defaultGroupName: String,
        private val config: RealtimeConfigService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

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

    ): Unit {
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
                        profile.surname + " " + profile.givenName,
                        "",
                        profile.mail ?: "",
                        RoleType.BASIC,
                        grantSellProduct = false, grantSellFood = false, grantSellMerch = false, grantMedia = false,
                        grantRateAchievement = false, grantCreateAchievement = false, grantListUsers = false,
                        grantGroupManager = false, grantGroupDebtsMananger = false,
                        grantFinance = false, grantTracker = false, grantRiddle = false,
                        groupName = "", group = null, guild = GuildType.UNKNOWN, major = MajorType.UNKNOWN
                )
                log.info("Logging in with new user ${user.fullName} pekId: ${user.pekId}")
            }
            updateFields(user, profile)
            auth = UsernamePasswordAuthenticationToken(code, state, getAuthorities(user))

            request.getSession(true).maxInactiveInterval = SESSION_TIMEOUT
            request.getSession(true).setAttribute(USER_SESSION_ATTRIBUTE_NAME, user.pekId)
            request.getSession(true).setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, user)

            val cookie = Cookie(LOGGED_IN_COOKIE, "true")
            cookie.maxAge = SESSION_TIMEOUT
            cookie.path = "/"
            httpResponse.addCookie(cookie)

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

    private fun updateFields(user: UserEntity, profile: ProfileDataResponse) {
        if (user.g7id.isBlank()) {
            profileService.generateProfileForUser(user)
        }
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
        if (profile.eduPersonEntitlements != null && user.role == RoleType.BASIC) {
            profile.eduPersonEntitlements
                    .filter { it.end == null }
                    .filter { it.name.equals(grantStaffGroupName) }
                    .forEach { _ ->
                        log.info("Granting STAFF for ${user.fullName}")
                        user.role = RoleType.STAFF

                        if (user.groupName.isBlank()) {
                            groups.findByName(staffGroupName).ifPresent {
                                user.groupName = it.name
                                user.group = it
                            }
                        }
                    }
        }
        if (systemAdmins.split(",").contains(user.pekId)) {
            log.info("Granting SUPERUSER for ${user.fullName}")
            user.role = RoleType.SUPERUSER
        }
        if (user.groupName.isBlank()) {
            groups.findByName(defaultGroupName).ifPresent {
                user.groupName = it.name
                user.group = it
            }
        }
        users.save(user)
    }

    private fun getAuthorities(user: UserEntity): List<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        return authorities
    }

    @ApiOperation("Redirection to the auth provider")
    @GetMapping("/control/login")
    fun items(request: HttpServletRequest): String {
        if (config.isRequestForNeptun()) {
            return "redirect:" + authSch.generateLoginUrl(buildUniqueState(request),
                    Scope.BASIC, Scope.NEPTUN_CODE, Scope.SURNAME, Scope.GIVEN_NAME, Scope.EDU_PERSON_ENTILEMENT)
        }
        return "redirect:" + authSch.generateLoginUrl(buildUniqueState(request),
                Scope.BASIC, Scope.SURNAME, Scope.GIVEN_NAME, Scope.MAIL, Scope.EDU_PERSON_ENTILEMENT)
    }

    fun buildUniqueState(request: HttpServletRequest): String {
        return (request.session.id
                + request.localAddr).sha256()
    }

    @ApiOperation("Logout user")
    @GetMapping("/control/logout")
    fun logout(request: HttpServletRequest, httpResponse: HttpServletResponse): String {
        log.info("Logging out from user {}", request.getUserOrNull()?.fullName ?: "n/a")

        try {
            request.getSession(false)

            SecurityContextHolder.clearContext()
            val session = request.getSession(false)
            session?.invalidate()
            for (cookie in request.cookies) {
                cookie.value = ""
                cookie.maxAge = 0
                cookie.path = "/"
                httpResponse.addCookie(cookie)
            }

            request.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
            request.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
            request.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
            request.session.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
            request.session.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
            request.session.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
            request.changeSessionId()
        } catch (e: Exception) {
            // Ignore it for now
        }
        return "redirect:${config.getWebsiteUrl()}"
    }

    @GetMapping("/control/open-site")
    fun openSite(request: HttpServletRequest): String {
        if (config.isEventFinished())
            return "redirect:/"
        return "redirect:" + config.getWebsiteUrl()
    }

    @ResponseBody
    @GetMapping("/control/test")
    fun test(): String {
        log.info("test endpoint was fired")
        return "Pong!"
    }

    @ResponseBody
    @GetMapping("/control/test-user")
    fun testUser(request: HttpServletRequest): String {
        log.info("test user endpoint was fired")
        return request.getUserOrNull()?.fullName ?: "not logged in"
    }

}
