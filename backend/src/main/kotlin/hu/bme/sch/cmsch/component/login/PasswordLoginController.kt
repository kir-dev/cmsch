package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.email.EmailService
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI
import java.util.*

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
    val token: String? = null,
    val message: String? = null
)

@RestController
@RequestMapping("/api")
class PasswordLoginController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
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

    @Operation(summary = "Login with email and password")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Login successful"),
        ApiResponse(responseCode = "401", description = "Invalid credentials")
    ])
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse, servletRequest: HttpServletRequest): LoginResponse {
        if (!loginComponent.passwordEnabled) {
            return LoginResponse("error", message = "Password login is disabled")
        }

        val ip = servletRequest.remoteAddr
        if (!rateLimiterService.isAllowed(ip, loginComponent.loginRateLimit)) {
            return LoginResponse("error", message = "Too many login attempts. Please try again later.")
        }

        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            val userDetails = authentication.principal as CmschUserDetails
            val userEntity = userDetails.userEntity
            
            val token = jwtTokenProvider.createToken(userEntity)
            
            val cookie = Cookie("jwt", token)
            cookie.path = "/"
            cookie.isHttpOnly = true
            // cookie.secure = true // Enable in production if needed
            cookie.maxAge = 60 * 60 * 24 * 7 // 1 week
            response.addCookie(cookie)

            auditLogService.login(userEntity, "password user login g:${userEntity.group} r:${userEntity.role}")
            
            LoginResponse("ok", token)
        } catch (e: DisabledException) {
            LoginResponse("error", message = "Email is not confirmed yet")
        } catch (e: Exception) {
            log.error("Login failed for user ${request.email}", e)
            LoginResponse("error", message = "Invalid email or password")
        }
    }

    @Operation(summary = "Register with email and password")
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): LoginResponse {
        if (!loginComponent.passwordEnabled) {
            return LoginResponse("error", message = "Registration is disabled")
        }

        if (loginComponent.captchaEnabled) {
            if (request.captchaToken == null || !verifyCaptcha(request.captchaToken)) {
                return LoginResponse("error", message = "Invalid captcha")
            }
        }

        if (request.email.isBlank() || request.password.isBlank() || request.fullName.isBlank()) {
            return LoginResponse("error", message = "All fields are required")
        }

        if (userRepository.findByEmail(request.email).isPresent) {
            return LoginResponse("error", message = "Email already exists")
        }

        val internalId = UUID.randomUUID().toString()
        val confirmationToken = if (loginComponent.emailConfirmationEnabled) UUID.randomUUID().toString() else null
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
        
        if (loginComponent.emailConfirmationEnabled && confirmationToken != null) {
            sendConfirmationEmail(user, confirmationToken)
        }
        
        log.info("Registered new user ${user.fullName} with email ${user.email}")
        return LoginResponse("ok", message = if (loginComponent.emailConfirmationEnabled) "Registration successful. Please check your email to confirm your account." else "Registration successful")
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

    @Operation(summary = "Confirm email address")
    @GetMapping("/confirm-email")
    fun confirmEmail(@RequestParam token: String): ResponseEntity<Void> {
        val user = userRepository.findAll().find { it.confirmationToken == token }
            ?: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("${loginComponent.externalUrl}/login?error=invalid-token")).build()
        
        user.emailConfirmed = true
        user.confirmationToken = null
        userService.save(user)
        
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("${loginComponent.externalUrl}/login?confirmed=true")).build()
    }

    @Operation(summary = "Request password reset")
    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse("error", message = "Forgot password is disabled")
        }

        val user = userRepository.findByEmail(request.email).orElse(null)
            ?: return LoginResponse("ok", message = "If the email exists, a reset link has been sent.")

        val token = UUID.randomUUID().toString()
        user.passwordResetToken = token
        user.passwordResetTokenExpiration = System.currentTimeMillis() + 1000 * 60 * 60 // 1 hour
        userService.save(user)

        sendPasswordResetEmail(user, token)

        return LoginResponse("ok", message = "If the email exists, a reset link has been sent.")
    }

    @Operation(summary = "Reset password with token")
    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: ResetPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse("error", message = "Forgot password is disabled")
        }

        val user = userRepository.findAll().find { it.passwordResetToken == request.token }
            ?: return LoginResponse("error", message = "Invalid or expired token")

        val expiration = user.passwordResetTokenExpiration ?: 0
        if (expiration < System.currentTimeMillis()) {
            return LoginResponse("error", message = "Token has expired")
        }

        user.password = passwordEncoder.encode(request.newPassword)
        user.passwordResetToken = null
        user.passwordResetTokenExpiration = null
        userService.save(user)

        return LoginResponse("ok", message = "Password has been reset successfully")
    }

    private fun sendConfirmationEmail(user: UserEntity, token: String) {
        emailService.ifPresent { service ->
            val template = service.getTemplateBySelector(loginComponent.emailConfirmationTemplate)
            if (template != null) {
                val values = mapOf(
                    "name" to user.fullName,
                    "link" to "${loginComponent.externalUrl}/api/confirm-email?token=$token"
                )
                service.sendTemplatedEmail(null, template, values, listOf(user.email))
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
}
