package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.token.SESSION_TOKEN_COLLECTOR_ATTRIBUTE
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.JwtTokenProvider
import hu.bme.sch.cmsch.util.getUserOrNull
import org.apache.catalina.util.URLEncoder
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Controller
@ConditionalOnBean(LoginComponent::class)
class AuthschLoginController(
    private val applicationComponent: ApplicationComponent,
    private val jwtTokenProvider: JwtTokenProvider,
    private val startupPropertyConfig: StartupPropertyConfig,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val encoder = URLEncoder()

    @ResponseBody
    @GetMapping("/control/logged-out")
    fun loggedOut(): String {
        return "Sikeres kijelentkezés!"
    }

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
    fun loginDefault(request: HttpServletRequest): String {
        return "redirect:${applicationComponent.siteUrl.getValue()}login"
    }

    @GetMapping("/control/logout")
    fun logout(request: HttpServletRequest, auth: Authentication?, httpResponse: HttpServletResponse): String {
        log.info("Logging out from user {}", auth?.getUserOrNull()?.internalId ?: "n/a")

        try {
            SecurityContextHolder.getContext().authentication = null
            val session = request.getSession(false)
            session?.invalidate()
            request.changeSessionId()

        } catch (e: Exception) {
            // It should be logged out anyway
        }
        return "redirect:${applicationComponent.siteUrl.getValue()}?logged-out=true"
    }

    @GetMapping("/control/open-site")
    fun openSite(auth: Authentication?, request: HttpServletRequest): String {
        if (auth != null && startupPropertyConfig.jwtEnabled) {
            if (auth.principal !is CmschUser) {
                log.error("User is not CmschUser {} {}", auth, auth.javaClass.simpleName)
                return "redirect:${applicationComponent.siteUrl.getValue()}?error=cannot-generate-jwt"
            }
            val jwtToken = jwtTokenProvider.createToken(auth.principal as CmschUser)

            return "redirect:${applicationComponent.siteUrl.getValue()}?jwt=${encoder.encode(jwtToken, StandardCharsets.UTF_8)}"
        }
        return "redirect:${applicationComponent.siteUrl.getValue()}"
    }

    @ResponseBody
    @CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
    @PostMapping("/api/control/refresh")
    fun refreshToken(auth: Authentication?): ResponseEntity<String> {
        if (auth == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        if (!startupPropertyConfig.jwtEnabled)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT not enabled")

        return ResponseEntity.ok(jwtTokenProvider.refreshToken(auth))
    }

}
