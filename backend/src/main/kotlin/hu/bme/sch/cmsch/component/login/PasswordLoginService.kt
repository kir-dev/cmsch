package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.email.EmailMode
import hu.bme.sch.cmsch.component.email.EmailService
import hu.bme.sch.cmsch.component.email.EmailTemplateEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
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

enum class LoginStatus {
    OK,
    ERROR,
    INVALID_CAPTCHA,
    MISSING_FIELDS,
    EMAIL_ALREADY_EXISTS,
    EMAIL_NOT_CONFIRMED,
    INVALID_CREDENTIALS,
    DISABLED,
    RATE_LIMITED,
    INVALID_TOKEN,
    TOKEN_EXPIRED
}

data class LoginResponse(
    val status: LoginStatus,
    val message: String? = null,
    val token: String? = null,
    val emailConfirmed: Boolean? = true
)

@Service
class PasswordLoginService(
    private val appComponent: ApplicationComponent,
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

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun login(request: LoginRequest, ip: String): LoginResponse {
        if (!loginComponent.passwordEnabled) {
            return LoginResponse(LoginStatus.DISABLED, message = "A jelszavas bejelentkezés le van tiltva")
        }

        if (!rateLimiterService.isAllowed(ip, loginComponent.loginRateLimit)) {
            return LoginResponse(LoginStatus.RATE_LIMITED,
                message = "Túl sok bejelentkezési kísérlet. Kérjük, próbálkozzon később.")
        }

        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            val userDetails = authentication.principal as CmschUserDetails
            val userEntity = userDetails.userEntity

            val token = jwtTokenProvider.createToken(userEntity)
            auditLogService.login(userEntity, "password user login g:${userEntity.group} r:${userEntity.role}")

            LoginResponse(LoginStatus.OK, token = token, emailConfirmed = userEntity.emailConfirmed)
        } catch (e: DisabledException) {
            LoginResponse(LoginStatus.EMAIL_NOT_CONFIRMED,
                message = "Az e-mail cím még nincs megerősítve",
                emailConfirmed = false)
        } catch (e: Exception) {
            log.error("Login failed for user ${request.email}", e)
            LoginResponse(LoginStatus.INVALID_CREDENTIALS, message = "Érvénytelen e-mail cím vagy jelszó")
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun register(request: RegisterRequest): LoginResponse {
        if (!loginComponent.passwordEnabled) {
            return LoginResponse(LoginStatus.DISABLED, message = "A regisztráció le van tiltva")
        }

        if (loginComponent.captchaEnabled) {
            if (request.captchaToken == null || !verifyCaptcha(request.captchaToken)) {
                return LoginResponse(LoginStatus.INVALID_CAPTCHA, message = "Érvénytelen captcha")
            }
        }

        if (request.email.isBlank() || request.password.isBlank() || request.fullName.isBlank()) {
            return LoginResponse(LoginStatus.MISSING_FIELDS, message = "Minden mező kitöltése kötelező")
        }

        if (userRepository.findByEmailIgnoreCase(request.email).isPresent) {
            return LoginResponse(LoginStatus.EMAIL_ALREADY_EXISTS, message = "Ez az e-mail cím már létezik")
        }

        val internalId = Uuid.generateV7().toString()
        var confirmationToken: String? = null
        val emailConfirmed = !loginComponent.emailConfirmationEnabled
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
            emailConfirmed = emailConfirmed,
            confirmationToken = confirmationToken
        )

        profileService.generateProfileIdForUser(user)
        userService.save(user)

        var token: String? = null
        if (!loginComponent.emailConfirmationEnabled) {
            token = jwtTokenProvider.createToken(user)
        }

        log.info("Registered new user ${user.fullName} with email ${user.email}")
        return LoginResponse(LoginStatus.OK,
            message = if (loginComponent.emailConfirmationEnabled) "Sikeres regisztráció. Kérjük, ellenőrizze az e-mail fiókját a fiók megerősítéséhez." else "Sikeres regisztráció",
            token = token,
            emailConfirmed = emailConfirmed)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun confirmEmail(token: String): UserEntity? {
        val user = userRepository.findAll().find { it.confirmationToken == token } ?: return null

        user.emailConfirmed = true
        user.confirmationToken = null
        userService.save(user)

        auditLogService.login(user, "password user confirmed email g:${user.group} r:${user.role}")
        return user
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun forgotPassword(request: ForgotPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse(LoginStatus.DISABLED, message = "Az elfelejtett jelszó funkció le van tiltva")
        }

        val user = userRepository.findByEmailIgnoreCase(request.email).orElse(null)
            ?: return LoginResponse(LoginStatus.OK,
                message = "Ha az e-mail cím létezik, elküldtük a jelszó-visszaállítási linket.")

        val token = Uuid.generateV7().toString()
        user.passwordResetToken = token
        user.passwordResetTokenExpiration = System.currentTimeMillis() + 1000 * 60 * 60 // 1 hour
        userService.save(user)

        sendPasswordResetEmail(user, token)

        return LoginResponse(LoginStatus.OK,
            message = "Ha az e-mail cím létezik, elküldtük a jelszó-visszaállítási linket.")
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun resetPassword(request: ResetPasswordRequest): LoginResponse {
        if (!loginComponent.forgotPasswordEnabled) {
            return LoginResponse(LoginStatus.DISABLED, message = "Az elfelejtett jelszó funkció le van tiltva")
        }

        val user = userRepository.findAll().find { it.passwordResetToken == request.token }
            ?: return LoginResponse(LoginStatus.INVALID_TOKEN, message = "Érvénytelen vagy lejárt token")

        val expiration = user.passwordResetTokenExpiration ?: 0
        if (expiration < System.currentTimeMillis()) {
            return LoginResponse(LoginStatus.TOKEN_EXPIRED, message = "A token lejárt")
        }

        user.password = passwordEncoder.encode(request.newPassword)
        user.passwordResetToken = null
        user.passwordResetTokenExpiration = null
        userService.save(user)

        return LoginResponse(LoginStatus.OK, message = "A jelszó sikeresen megváltoztatva")
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

    private fun sendConfirmationEmail(email: String, fullName: String, token: String) {
        val service = emailService.orElseThrow()
        val templateName = loginComponent.emailConfirmationTemplate
        val template = service.getTemplateBySelector(templateName)
        if (template == null && templateName.isNotBlank()) {
            log.error("Email confirmation email template '{}' could not be found!", templateName)
        }
        val values = mapOf(
            "name" to fullName,
            "link" to "${loginComponent.externalUrl}/api/confirm-email?token=$token"
        )
        service.sendTemplatedEmail(null, template ?: getDefaultEmailConfirmationTemplate(), values, listOf(email))
    }

    private fun sendPasswordResetEmail(user: UserEntity, token: String) {
        val service = emailService.orElseThrow()
        val templateName = loginComponent.passwordResetTemplate
        val template = service.getTemplateBySelector(templateName)
        if (template == null && templateName.isNotBlank()) {
            log.error("Password reset email template '{}' could not be found!", templateName)
        }
        val values = mapOf(
            "name" to user.fullName,
            "link" to "${loginComponent.externalUrl}/reset-password?token=$token"
        )
        service.sendTemplatedEmail(null, template ?: getDefaultPasswordResetTemplate(), values, listOf(user.email))
    }

    private fun getDefaultPasswordResetTemplate() = EmailTemplateEntity(
        subject = "Jelszó visszaállítás - ${appComponent.siteName}",
        template = """
            <html>
                <body>
                    <h1>Kedves {{name}}!</h1>
                    <p>Azért kaptad ezt az üzenetet, mert jelszó-visszaállítást kértél a ${appComponent.siteName} oldalon.</p>
                    <p>A jelszavadat az alábbi linkre kattintva állíthatod vissza:</p>
                    <p><a href="{{link}}">Jelszó visszaállítása</a></p>
                    <p>Ha nem te kérted a visszaállítást, hagyd figyelmen kívül ezt az üzenetet!</p>
                    <p>Üdvözlettel,<br>A ${appComponent.siteName} csapata</p>
                </body>
            </html>
        """.trimIndent(),
        mode = EmailMode.HTML
    )

    private fun getDefaultEmailConfirmationTemplate() = EmailTemplateEntity(
        subject = "Regisztráció megerősítése - ${appComponent.siteName}",
        template = """
            <html>
                <body>
                    <h1>Kedves {{name}}!</h1>
                    <p>Köszönjük, hogy regisztráltál a ${appComponent.siteName} oldalon!</p>
                    <p>A regisztrációd befejezéséhez kérjük, erősítsd meg az e-mail címedet az alábbi linkre kattintva:</p>
                    <p><a href="{{link}}">E-mail cím megerősítése</a></p>
                    <p>Üdvözlettel,<br>A ${appComponent.siteName} csapata</p>
                </body>
            </html>
        """.trimIndent(),
        mode = EmailMode.HTML
    )

}
