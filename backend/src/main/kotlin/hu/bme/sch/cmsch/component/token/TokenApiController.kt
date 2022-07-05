package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.riddle.RiddleView
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest

const val SESSION_TOKEN_COLLECTOR_ATTRIBUTE = "TOKEN_COLLECTOR_ATTRIBUTE"

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(TokenComponent::class)
class TokenApiController(
    private val tokens: TokenCollectorService,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    @JsonView(FullDetails::class)
    @GetMapping("/tokens")
    fun riddle(auth: Authentication): ResponseEntity<TokenView> {
        val user = auth.getUserOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(tokens.getTokenViewForUser(user))
    }

    @ResponseBody
    @JsonView(FullDetails::class)
    @PostMapping("/token/{token}")
    fun submitToken(@PathVariable token: String, auth: Authentication): TokenSubmittedView {
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
    }

    @GetMapping("/token-after-login")
    fun submitTokenAfterLogin(request: HttpServletRequest, auth: Authentication): String {
        val token = request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE)?.toString()
        request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, null)
        return if (token == null) {
            "redirect:/"
        } else {
            return collectToken(auth, token)
        }
    }

    @GetMapping("/qr/{token}")
    fun readQrManually(@PathVariable token: String, request: HttpServletRequest, auth: Authentication): String {
        val user = auth.getUserOrNull()
        return if (user == null) {
            request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, token)
            "redirect:/control/login"
        } else {
            collectToken(auth, token)
        }
    }

    private fun collectToken(auth: Authentication, token: String) =
        when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> {
                val (title, status) = tokens.collectToken(auth.getUser(), token)
                "redirect:/qr-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
            OwnershipType.GROUP -> {
                val (title, status) = tokens.collectTokenForGroup(auth.getUserFromDatabase(), token)
                "redirect:/qr-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
        }

}
