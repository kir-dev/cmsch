package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @GetMapping("/app")
    fun app(auth: Authentication?): ApplicationConfigDto {
        val role = auth?.getUserOrNull()?.role ?: RoleType.GUEST
        if (countdownComponent.isPresent) {
            val countdown = countdownComponent.orElseThrow()
            if (countdown.isBlockedAt(clock.getTimeInSeconds())) {
                return ApplicationConfigDto(
                    role = role,
                    menu = listOf(),
                    components = mapOf(
                        applicationComponent.component to appComponentFields(),
                        countdown.component to countdown.attachConstants(),
                        stylingComponent.component to stylingComponent.attachConstants()
                    )
                )
            }
        }
        return ApplicationConfigDto(
            role = role,
            menu = menuService.getCachedMenuForRole(role),
            components = componentHandlerService.getComponentConstantsForRole(role)
        )
    }

    private fun appComponentFields() =
        mapOf(applicationComponent.defaultComponent.property to applicationComponent.defaultComponent.getValue())

}
