package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.TokenSubmittedView
import hu.bme.sch.cmsch.dto.view.WarningView
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.service.TokenCollectorService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest

const val SESSION_TOKEN_COLLECTOR_ATTRIBUTE = "TOKEN_COLLECTOR_ATTRIBUTE"

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class TokenApiController(
        private val tokens: TokenCollectorService
) {

    @JsonView(FullDetails::class)
    @PostMapping("/token/{token}")
    fun submitToken(@PathVariable token: String, request: HttpServletRequest): TokenSubmittedView {
        val (title, status) = tokens.collectToken(request.getUser(), token)
        return TokenSubmittedView(status, title)
    }

    @GetMapping("/token-after-login")
    fun submitTokenAfterLogin(request: HttpServletRequest): String {
        val token = request.getSession(true).getAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE)?.toString()
        request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, null)
        return if (token == null) {
            "redirect:/"
        } else {
            val (title, status) = tokens.collectToken(request.getUser(), token)
            "redirect:/qr-scanned?status=${status.name}&title=${URLEncoder.encode(title ?: "", StandardCharsets.UTF_8.toString())}"
        }
    }

    @GetMapping("/qr/{token}")
    fun readQrManually(@PathVariable token: String, request: HttpServletRequest): String {
        val user = request.getUserOrNull()
        return if (user == null) {
            request.getSession(true).setAttribute(SESSION_TOKEN_COLLECTOR_ATTRIBUTE, token)
            "redirect:/control/login"
        } else {
            val (title, status) = tokens.collectToken(user, token)
            "/qr-scanned?status=${status.name}&title=${URLEncoder.encode(title, StandardCharsets.UTF_8.toString())}"
        }
    }

}
