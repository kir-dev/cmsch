package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import jakarta.servlet.http.HttpServletRequest

const val SESSION_TOKEN_COLLECTOR_ATTRIBUTE = "TOKEN_COLLECTOR_ATTRIBUTE"

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(TokenComponent::class)
class TokenApiController(
    private val tokens: TokenCollectorService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val applicationComponent: ApplicationComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @JsonView(FullDetails::class)
    @GetMapping("/tokens")
    fun riddle(auth: Authentication?): ResponseEntity<TokenView> {
        val user = auth.getUserOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tokens.getTokenViewForUser(user))
    }

    @ResponseBody
    @JsonView(FullDetails::class)
    @PostMapping("/token/{token}")
    fun submitToken(@PathVariable token: String, auth: Authentication): TokenSubmittedView {
        try {
            return when (startupPropertyConfig.tokenOwnershipMode) {
                OwnershipType.USER -> {
                    val (title, status) = tokens.collectToken(auth.getUser(), token)
                    TokenSubmittedView(status, title)
                }
                OwnershipType.GROUP -> {
                    val (title, status) = tokens.collectTokenForGroup(auth.getUserFromDatabase(), token)
                    TokenSubmittedView(status, title)
                }
            }
        } catch (e: Throwable) {
            log.error("Failed to redeem token: '{}'", token, e)
            return TokenSubmittedView(TokenCollectorStatus.CANNOT_COLLECT, null)
        }
    }

    @GetMapping("/token-after-login")
    fun submitTokenAfterLogin(request: HttpServletRequest, auth: Authentication): String {
        val token = request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE)?.toString()
        request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, null)
        return if (token == null) {
            "redirect:${applicationComponent.siteUrl.getValue()}?error=failed-to-redeem"
        } else {
            collectToken(auth, token)
        }
    }

    @GetMapping("/qr/{token}")
    fun readQrManually(@PathVariable token: String, request: HttpServletRequest, auth: Authentication?): String {
        try {
            val user = auth?.getUserOrNull()
            return if (user == null) {
                request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, token)
                "redirect:${applicationComponent.siteUrl.getValue()}login"
            } else {
                collectToken(auth, token)
            }
        } catch (e: Throwable) {
            log.error("Failed to redeem token: '{}'", token, e)
            return "redirect:${applicationComponent.siteUrl.getValue()}login?error=failed-to-redeem"
        }
    }

    private fun collectToken(auth: Authentication, token: String): String {
        return when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> {
                val (title, status) = tokens.collectToken(auth.getUser(), token)
                log.info("Token collected for USER '{}' token '{}'", auth.getUser().userName, token)
                "redirect:${applicationComponent.siteUrl.getValue()}token-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
            OwnershipType.GROUP -> {
                val (title, status) = tokens.collectTokenForGroup(auth.getUserFromDatabase(), token)
                log.info("Token collected for GROUP by user '{}' token '{}'", auth.getUser().userName, token)
                "redirect:${applicationComponent.siteUrl.getValue()}token-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
        }
    }

}
