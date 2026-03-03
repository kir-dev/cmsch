package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.email.EmailService
import hu.bme.sch.cmsch.component.token.SESSION_TOKEN_COLLECTOR_ATTRIBUTE
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI
import java.util.*
import kotlin.uuid.Uuid

data class LoginRequest(
    val email: String = "",
    val password: String = ""
)

data class RegisterRequest(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val captchaToken: String? = null
)

data class ForgotPasswordRequest(
    val email: String = ""
)

data class ResetPasswordRequest(
    val token: String = "",
    val newPassword: String = ""
)

data class LoginResponse(
    val status: String,
    val message: String? = null
)

@Controller
@ConditionalOnBean(LoginComponent::class)
class LoginController(
    private val applicationComponent: ApplicationComponent,
    private val jwtTokenProvider: JwtTokenProvider,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val authenticationManager: AuthenticationManager,
    private val auditLogService: AuditLogService,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val profileService: UserProfileGeneratorService,
    private val loginComponent: LoginComponent,
    private val emailService: Optional<EmailService>,
    private val rateLimiterService: RateLimiterService,
    webClientBuilder: WebClient.Builder
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val captchaClient = webClientBuilder.baseUrl("https://www.google.com/recaptcha/api").build()

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
        if (!loginComponent.passwordEnabled) {
            return LoginResponse("error", message = "A jelszavas bejelentkezés le van tiltva")
        }

        val ip = servletRequest.remoteAddr
        if (!rateLimiterService.isAllowed(ip, loginComponent.loginRateLimit)) {
            return LoginResponse("error", message = "Túl sok bejelentkezési kísérlet. Kérjük, próbálkozzon később.")
        }
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
// TODOOOOOOOO
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            val userDetails = authentication.principal as CmschUserDetails
            val userEntity = userDetails.userEntity

            val token = jwtTokenProvider.createToken(userEntity)
            response.addCookie(createJwtCookie(token))

            auditLogService.login(userEntity, "password user login g:${userEntity.group} r:${userEntity.role}")

            LoginResponse("ok", token)
        } catch (e: DisabledException) {
            LoginResponse("error", message = "Az e-mail cím még nincs megerősítve")
        } catch (e: Exception) {
            log.error("Login failed for user ${request.email}", e)
            LoginResponse("error", message = "Érvénytelen e-mail cím vagy jelszó")
        }
    }

    @ResponseBody
    @PostMapping("/api/register")
    fun register(@RequestBody request: RegisterRequest, response: HttpServletResponse): LoginResponse {
        if (!loginComponent.passwordEnabled) {
            return LoginResponse("error", message = "A regisztráció le van tiltva")
        }

        if (loginComponent.captchaEnabled) {
            if (request.captchaToken == null || !verifyCaptcha(request.captchaToken)) {
                return LoginResponse("error", message = "Érvénytelen captcha")
            }
        }

        if (request.email.isBlank() || request.password.isBlank() || request.fullName.isBlank()) {
            return LoginResponse("error", message = "Minden mező kitöltése kötelező")
        }

        if (userRepository.findByEmailIgnoreCase(request.email).isPresent) {
            return LoginResponse("error", message = "Ez az e-mail cím már létezik")
        }

        val internalId = Uuid.generateV7().toString()
        var confirmationToken: String? = null
        if (loginComponent.emailConfirmationEnabled) {
            confirmationToken = Uuid.generateV7().toString()
            sendConfirmationEmail(request.email, request.fullName, confirmationToken)
        }
        val user = UserEntity(
            internalId = internalId,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            role = RoleType.BASIC,
            provider = "password",
            emailConfirmed = !loginComponent.emailConfirmationEnabled,
            confirmationToken = confirmationToken
        )

        profileService.generateProfileIdForUser(user)
        userService.save(user)

        if (!loginComponent.emailConfirmationEnabled) {
            val token = jwtTokenProvider.createToken(user)
            response.addCookie(createJwtCookie(token))
        }

        log.info("Registered new user ${user.fullName} with email ${user.email}")
        return LoginResponse("ok",
            message = if (loginComponent.emailConfirmationEnabled) "Sikeres regisztráció. Kérjük, ellenőrizze az e-mail fiókját a fiók megerősítéséhez." else "Sikeres regisztráció")
    }

    private fun verifyCaptcha(token: String): Boolean {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("secret", loginComponent.captchaSecretKey)
        formData.add("response", token)

        val response = captchaClient.post()
            .uri("/siteverify")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        return response?.get("success") == true
    }

    @ResponseBody
    @GetMapping("/api/confirm-email")
    fun confirmEmail(@RequestParam token: String): ResponseEntity<Void> {
        val user = userRepository.findAll().find { it.confirmationToken == token }
            ?: return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("${loginComponent.externalUrl}/login?error=invalid-token")).build()

        user.emailConfirmed = true
        user.confirmationToken = null
        userService.save(user)

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("${loginComponent.externalUrl}/login?confirmed=true")).build()
    }

    @ResponseBody
    @PostMapping("/api/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse("error", message = "Az elfelejtett jelszó funkció le van tiltva")
        }

        val user = userRepository.findByEmailIgnoreCase(request.email).orElse(null)
            ?: return LoginResponse("ok",
                message = "Ha az e-mail cím létezik, elküldtük a jelszó-visszaállítási linket.")

        val token = Uuid.generateV7().toString()
        user.passwordResetToken = token
        user.passwordResetTokenExpiration = System.currentTimeMillis() + 1000 * 60 * 60 // 1 hour
        userService.save(user)

        sendPasswordResetEmail(user, token)

        return LoginResponse("ok", message = "Ha az e-mail cím létezik, elküldtük a jelszó-visszaállítási linket.")
    }

    @ResponseBody
    @PostMapping("/api/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse("error", message = "Az elfelejtett jelszó funkció le van tiltva")
        }

        val user = userRepository.findAll().find { it.passwordResetToken == request.token }
            ?: return LoginResponse("error", message = "Érvénytelen vagy lejárt token")

        val expiration = user.passwordResetTokenExpiration ?: 0
        if (expiration < System.currentTimeMillis()) {
            return LoginResponse("error", message = "A token lejárt")
        }

        user.password = passwordEncoder.encode(request.newPassword)
        user.passwordResetToken = null
        user.passwordResetTokenExpiration = null
        userService.save(user)

        return LoginResponse("ok", message = "A jelszó sikeresen megváltoztatva")
    }

    private fun sendConfirmationEmail(email: String, fullName: String, token: String) {
        emailService.ifPresent { service ->
            val template = service.getTemplateBySelector(loginComponent.emailConfirmationTemplate)
            if (template != null) {
                val values = mapOf(
                    "name" to fullName,
                    "link" to "${loginComponent.externalUrl}/api/confirm-email?token=$token"
                )
                service.sendTemplatedEmail(null, template, values, listOf(email))
            } else {
                log.error("Confirmation email template not found: ${loginComponent.emailConfirmationTemplate}")
            }
        }
    }

    private fun sendPasswordResetEmail(user: UserEntity, token: String) {
        emailService.ifPresent { service ->
            val template = service.getTemplateBySelector(loginComponent.passwordResetTemplate)
            if (template != null) {
                val values = mapOf(
                    "name" to user.fullName,
                    "link" to "${loginComponent.externalUrl}/reset-password?token=$token"
                )
                service.sendTemplatedEmail(null, template, values, listOf(user.email))
            } else {
                log.error("Password reset email template not found: ${loginComponent.passwordResetTemplate}")
            }
        }
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
