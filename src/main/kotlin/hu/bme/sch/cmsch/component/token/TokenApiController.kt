package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.config.OwnershipType
import hu.bme.sch.cmsch.component.token.TokenSubmittedView
import hu.bme.sch.cmsch.component.token.TokenCollectorService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
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
    @Value("\${cmsch.token.ownership:USER}") private val tokenOwnershipMode: OwnershipType
) {

    @ResponseBody
    @JsonView(FullDetails::class)
    @PostMapping("/token/{token}")
    fun submitToken(@PathVariable token: String, request: HttpServletRequest): TokenSubmittedView {
        return when (tokenOwnershipMode) {
            OwnershipType.USER -> {
                val (title, status) = tokens.collectToken(request.getUser(), token)
                TokenSubmittedView(status, title)
            }
            OwnershipType.GROUP -> {
                val (title, status) = tokens.collectTokenForGroup(request.getUser(), token)
                TokenSubmittedView(status, title)
            }
        }
    }

    @GetMapping("/token-after-login")
    fun submitTokenAfterLogin(request: HttpServletRequest): String {
        val token = request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE)?.toString()
        request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, null)
        return if (token == null) {
            "redirect:/"
        } else {
            return collectToken(request, token)
        }
    }

    @GetMapping("/qr/{token}")
    fun readQrManually(@PathVariable token: String, request: HttpServletRequest): String {
        val user = request.getUserOrNull()
        return if (user == null) {
            request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, token)
            "redirect:/control/login"
        } else {
            collectToken(request, token)
        }
    }

    private fun collectToken(request: HttpServletRequest, token: String) =
        when (tokenOwnershipMode) {
            OwnershipType.USER -> {
                val (title, status) = tokens.collectToken(request.getUser(), token)
                "redirect:/qr-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
            OwnershipType.GROUP -> {
                val (title, status) = tokens.collectTokenForGroup(request.getUser(), token)
                "redirect:/qr-scanned?status=${status.name}" +
                        "&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
            }
        }

}
