package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class ApplicationApiController(
    private val menuService: MenuService,
    private val applicationComponent: ApplicationComponent,
    private val componentHandlerService: ComponentHandlerService,
    private val countdownComponent: Optional<CountdownComponent>,
    private val clock: TimeService,
    private val stylingComponent: StylingComponent
) {

    private val componentWriter = ObjectMapper().writerFor(object : TypeReference<Map<String, Map<String, Any>>>() {})

    @GetMapping("/app")
    fun app(auth: Authentication?): ApplicationConfigDto {
        val role = auth?.getUserOrNull()?.role ?: RoleType.GUEST
        if (countdownComponent.isPresent) {
            val countdown = countdownComponent.orElseThrow()
            if (countdown.isBlockedAt(clock.getTimeInSeconds())) {
                val components = mapOf(
                    applicationComponent.component to appComponentFields(),
                    countdown.component to countdown.attachConstants(),
                    stylingComponent.component to stylingComponent.attachConstants()
                )
                return ApplicationConfigDto(
                    role = role,
                    menu = listOf(),
                    components = componentWriter.writeValueAsString(components)
                )
            }
        }
        return ApplicationConfigDto(
            role = role,
            menu = menuService.getCachedMenuForRole(role),
            components = componentHandlerService.getComponentConstantsForRoleFast(role)
        )
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
