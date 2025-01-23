package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
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
    private val components: List<ComponentBase>
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
                    components = componentWriter.writeValueAsString(components)
                )
            }
        }
        val components = components
            .filter { it.minRole.isAvailableForRole(role) || role.isAdmin }
            .associate { it.component to it.attachConstants() }
        return ApplicationConfigDto(
            role = role,
            menu = menuService.getCachedMenuForRole(role),
            components = componentWriter.writeValueAsString(components)
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

        val actualUser = auth.getUserEntityFromDatabaseOrNull()
        return ResponseEntity.ok(applicationService.getUserAuthInfo(jwtUser, actualUser))
    }

    private fun appComponentFields() =
        mapOf(applicationComponent.defaultComponent.property to applicationComponent.defaultComponent.getValue())

    @GetMapping("/app/font-display.css")
    fun getDisplayFont(): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.location = URI.create(stylingComponent.displayFontCdn.rawValue)
        return ResponseEntity.status(HttpStatusCode.valueOf(303)).headers(headers).build()
    }

    @GetMapping("/app/font-main.css")
    fun getMainFont(): ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.location = URI.create(stylingComponent.mainFontCdn.rawValue)
        return ResponseEntity.status(HttpStatusCode.valueOf(303)).headers(headers).build()
    }

}
