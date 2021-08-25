package hu.bme.sch.g7.controller
import hu.bme.sch.g7.dao.GroupRepository
import hu.bme.sch.g7.dao.GroupToUserMappingRepository
import hu.bme.sch.g7.dao.GuildToUserMappingRepository
import hu.bme.sch.g7.model.GuildType
import hu.bme.sch.g7.model.MajorType
import hu.bme.sch.g7.model.RoleType
import hu.bme.sch.g7.model.UserEntity
import hu.bme.sch.g7.service.UserProfileGeneratorService
import hu.bme.sch.g7.service.UserService
import hu.bme.sch.g7.util.sha256
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
import javax.servlet.http.HttpServletRequest

const val USER_SESSION_ATTRIBUTE_NAME = "user_id"
const val USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME = "user"
const val CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME = "circles"

@Controller
open class LoginController(
        private val authSch: AuthSchAPI,
        private val users: UserService,
        private val profileService: UserProfileGeneratorService,
        private val groupToUserMapping: GroupToUserMappingRepository,
        private val guildToUserMapping: GuildToUserMappingRepository,
        private val groups: GroupRepository,
        @Value("\${g7web.pek-group-grant-name:Szent Schönherz Senior Lovagrend}") private val grantStaffGroupName: String,
        @Value("\${g7web.sysadmins:}") private val systemAdmins: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @ResponseBody
    @GetMapping("/logged-out")
    fun loggedOut(): String {
        return "Sikeres kijelentkezés!"
    }

    @ApiOperation("Login re-entry point")
    @GetMapping("/auth/authsch/callback")
    fun loggedIn(@RequestParam code: String, @RequestParam state: String, request: HttpServletRequest): String {
        if (buildUniqueState(request) != state)
            return "index?invalid-state"

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
                        profile.neptun,
                        "",
                        profile.surname + " " + profile.givenName,
                        "",
                        "",
                        RoleType.BASIC,
                        false, false, false, false,
                        false, false,  false,
                        false, false,
                        false, false,
                        "", null, GuildType.UNKNOWN, MajorType.UNKNOWN
                )
                log.info("Logging in with new user ${user.fullName} pekId: ${user.pekId}")
            }
            updateFields(user, profile)
            auth = UsernamePasswordAuthenticationToken(code, state, getAuthorities(user))

            request.getSession(true).setAttribute(USER_SESSION_ATTRIBUTE_NAME, user.pekId)
            request.getSession(true).setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, user)
            SecurityContextHolder.getContext().authentication = auth
        } catch (e: Exception) {
            auth?.isAuthenticated = false
            e.printStackTrace()
        }
        return if (auth != null && auth.isAuthenticated) "redirect:/entrypoint" else "redirect:/logged-out?error"
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
                    .forEach {
                        log.info("Granting STAFF for ${user.fullName}")
                        user.role = RoleType.STAFF
                    }
        }
        if (systemAdmins.split(",").contains(user.pekId)) {
            log.info("Granting SUPERUSER for ${user.fullName}")
            user.role = RoleType.SUPERUSER
        }
        users.save(user)
    }

    private fun getAuthorities(user: UserEntity): List<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        return authorities
    }

    @ApiOperation("Redirection to the auth provider")
    @GetMapping("/login")
    fun items(request: HttpServletRequest): String {
        return "redirect:" + authSch.generateLoginUrl(buildUniqueState(request),
                Scope.BASIC, Scope.NEPTUN_CODE, Scope.SURNAME, Scope.GIVEN_NAME, Scope.EDU_PERSON_ENTILEMENT)
    }

    fun buildUniqueState(request: HttpServletRequest): String {
        return (request.session.id
                + request.localAddr).sha256()
    }

    @ApiOperation("Logout user")
    @GetMapping("/admin/logout")
    fun logout(request: HttpServletRequest): String {
        request.getSession(false)
        SecurityContextHolder.clearContext()
        val session = request.getSession(false)
        session?.invalidate()
        for (cookie in request.cookies) {
            cookie.maxAge = 0
        }

        request.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
        request.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
        request.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
        request.changeSessionId()
        return "redirect:/logged-out"
    }

}