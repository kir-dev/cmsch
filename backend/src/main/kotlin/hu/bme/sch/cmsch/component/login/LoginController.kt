package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.token.SESSION_TOKEN_COLLECTOR_ATTRIBUTE
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUserOrNull
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.util.descriptor.web.Constants
import org.apache.tomcat.util.http.SameSiteCookies
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URI

@Controller
@ConditionalOnBean(LoginComponent::class)
class LoginController(
    private val applicationComponent: ApplicationComponent,
    private val jwtTokenProvider: JwtTokenProvider,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val loginComponent: LoginComponent,
    private val passwordLoginService: PasswordLoginService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/control/post-login")
    fun postLogin(request: HttpServletRequest, httpResponse: HttpServletResponse, auth: Authentication?) {
        if (request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE) != null) {
            httpResponse.sendRedirect("/api/token-after-login")
            return
        }
        httpResponse.sendRedirect(
            if (auth != null && auth.isAuthenticated)
                "/control/open-site"
            else
                "/control/logged-out?error=not-logged-in"
        )
    }

    @GetMapping("/control/login")
    fun loginDefault(): String {
        return "redirect:${applicationComponent.siteUrl}login"
    }

    @GetMapping("/control/logout")
    fun logout(request: HttpServletRequest, auth: Authentication?, httpResponse: HttpServletResponse): String {
        log.info("Logging out from user {}", auth?.getUserOrNull()?.internalId ?: "n/a")

        try {
            httpResponse.addCookie(createJwtCookie(null).apply { maxAge = 0 })
            SecurityContextHolder.getContext().authentication = null
            val session = request.getSession(false)
            session?.invalidate()
            request.changeSessionId()

        } catch (e: Exception) {
            // It should be logged out anyway
        }
        return "redirect:${applicationComponent.siteUrl}?logged-out=true"
    }

    @GetMapping("/control/open-site")
    fun openSite(auth: Authentication?, response: HttpServletResponse): String {
        if (auth != null) {
            if (auth.principal !is CmschUser) {
                log.error("User is not CmschUser {} {}", auth, auth.javaClass.simpleName)
                return "redirect:${applicationComponent.siteUrl}?error=cannot-generate-jwt"
            }
            val jwtToken = jwtTokenProvider.createToken(auth.principal as CmschUser)

            response.addCookie(createJwtCookie(jwtToken))

            return "redirect:${applicationComponent.siteUrl}"
        }
        return "redirect:${applicationComponent.siteUrl}"
    }

    @ResponseBody
    @PostMapping("/api/control/refresh")
    fun refreshToken(auth: Authentication?, response: HttpServletResponse): ResponseEntity<String> {
        if (auth == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        response.addCookie(createJwtCookie(jwtTokenProvider.refreshToken(auth)))

        return ResponseEntity.ok().build()
    }

    @ResponseBody
    @PostMapping("/api/login")
    fun login(
        @RequestBody request: LoginRequest,
        response: HttpServletResponse,
        servletRequest: HttpServletRequest
    ): LoginResponse {
        val loginResponse = passwordLoginService.login(request, servletRequest.remoteAddr)
        if (loginResponse.status == LoginStatus.OK && loginResponse.token != null) {
            response.addCookie(createJwtCookie(loginResponse.token))
        }
        return loginResponse
    }

    @ResponseBody
    @PostMapping("/api/register")
    fun register(@RequestBody request: RegisterRequest, response: HttpServletResponse): LoginResponse {
        val loginResponse = passwordLoginService.register(request)
        if (loginResponse.status == LoginStatus.OK && loginResponse.token != null) {
            response.addCookie(createJwtCookie(loginResponse.token))
        }
        return loginResponse
    }

    @ResponseBody
    @GetMapping("/api/confirm-email")
    fun confirmEmail(@RequestParam token: String, response: HttpServletResponse): ResponseEntity<Void> {
        val user = passwordLoginService.confirmEmail(token)
            ?: return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("${applicationComponent.siteUrl}login?error=invalid-token")).build()

        val jwtToken = jwtTokenProvider.createToken(user)
        response.addCookie(createJwtCookie(jwtToken))

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("${applicationComponent.siteUrl}login?confirmed=true")).build()
    }

    @ResponseBody
    @PostMapping("/api/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): LoginResponse {
        return passwordLoginService.forgotPassword(request)
    }

    @ResponseBody
    @PostMapping("/api/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): LoginResponse {
        return passwordLoginService.resetPassword(request)
    }

    private fun getDomainFromUrl(url: String): String {
        return URI(url).host
    }

    private fun createJwtCookie(value: String?): Cookie {
        return Cookie("jwt", value).apply {
            isHttpOnly = true
            path = "/"
            maxAge = startupPropertyConfig.sessionValiditySeconds.toInt()
            secure = true
            domain = getDomainFromUrl(applicationComponent.siteUrl)
            setAttribute(Constants.COOKIE_SAME_SITE_ATTR, SameSiteCookies.LAX.value)
        }
    }
}
