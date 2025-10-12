package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonView
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
@RequestMapping("/api")
class ApplicationApiController(
    private val menuService: MenuService,
    private val applicationComponent: ApplicationComponent,
    private val countdownComponent: Optional<CountdownComponent>,
    private val clock: TimeService,
    private val stylingComponent: StylingComponent,
    private val applicationService: ApplicationService,
    private val components: List<ComponentBase>,
    private val userService: UserService,
) {

    private val componentWriter = ObjectMapper().writerFor(object : TypeReference<Map<String, Map<String, Any>>>() {})

    @GetMapping("/app")
    fun app(auth: Authentication?): ApplicationConfigDto {
        val role = auth?.getUserOrNull()?.role ?: RoleType.GUEST
        if (countdownComponent.isPresent) {
            val countdown = countdownComponent.orElseThrow()
            if (countdown.isBlockedAt(clock.getTimeInSeconds(), role)) {
                val countdownSettings = countdown.attachConstants().toMutableMap().also { it["showOnly"] = true }

                // Remove any image urls from the styles, because it might be a spoiler
                val styleSettings = stylingComponent.attachConstants().filter { !it.key.lowercase().contains("url") }
                val components = mapOf(
                    applicationComponent.component to appComponentFields(),
                    countdown.component to countdownSettings,
                    stylingComponent.component to styleSettings
                )
                return ApplicationConfigDto(
                    role = role,
                    menu = listOf(),
                    components = components
                )
            }
        }
        val components = components
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .associate { it.component to it.attachConstants() }
        return ApplicationConfigDto(
            role = role,
            menu = menuService.getCachedMenuForRole(role),
            components = components
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/whoami")
    @Operation(summary = "Show authentication info")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "The user is authenticated and data is provided or " +
                        "no valid token present (in this case the loggedIn is false)"
            ),
            ApiResponse(
                responseCode = "403", description = "Valid token is provided but the endpoint is not available " +
                        "for the role that the user have"
            )
        ]
    )
    fun profile(auth: Authentication?): ResponseEntity<UserAuthInfoView> {
        val jwtUser = auth?.getUserOrNull()
            ?: return ResponseEntity.ok(UserAuthInfoView(authState = AuthState.LOGGED_OUT))

        val actualUser = auth.getUserEntityFromDatabaseOrNull(userService)
        return ResponseEntity.ok(applicationService.getUserAuthInfo(jwtUser, actualUser))
    }

    private fun appComponentFields(): Map<String, Any> =
        mapOf("defaultComponent" to applicationComponent.defaultComponent)

    @GetMapping("/app/font-display.css")
    fun getDisplayFont(): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.location = URI.create(stylingComponent.displayFontCdn)
        return ResponseEntity.status(HttpStatusCode.valueOf(303)).headers(headers).build()
    }

    @GetMapping("/app/font-main.css")
    fun getMainFont(): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.location = URI.create(stylingComponent.mainFontCdn)
        return ResponseEntity.status(HttpStatusCode.valueOf(303)).headers(headers).build()
    }

    @GetMapping("/app/render-qr")
    fun renderQr(response: HttpServletResponse, @RequestParam text: String?, @RequestParam size: Int = 300) {
        if (text.isNullOrBlank() || size < 20) {
            response.status = HttpStatus.BAD_REQUEST.value()
            return
        }

        val qrHints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.CHARACTER_SET to StandardCharsets.UTF_8.toString(),
            EncodeHintType.MARGIN to 1
        )
        val matrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, qrHints)

        val imageFormat = "PNG"
        response.contentType = MediaType.IMAGE_PNG_VALUE
        MatrixToImageWriter.writeToStream(matrix, imageFormat, response.outputStream)
    }
}
