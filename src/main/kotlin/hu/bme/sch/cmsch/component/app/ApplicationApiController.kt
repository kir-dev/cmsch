package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.component.countdown.CountdownComponent
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ApplicationComponent::class)
class ApplicationApiController(
    private val menuService: MenuService,
    private val componentHandlerService: ComponentHandlerService,
    private val countdownComponent: Optional<CountdownComponent>,
    private val clock: TimeService
) {

    @GetMapping("/app")
    fun app(auth: Authentication): ApplicationConfigDto {
        val role = auth.getUserOrNull()?.role ?: RoleType.GUEST
        if (countdownComponent.isPresent) {
            val countdown = countdownComponent.orElseThrow()
            if (countdown.isBlockedAt(clock.getTimeInSeconds())) {
                return ApplicationConfigDto(
                    role,
                    listOf(),
                    mapOf(countdown.component to countdown.attachConstants()))
            }
        }
        return ApplicationConfigDto(
            role,
            menuService.getCachedMenuForRole(role),
            componentHandlerService.getComponentConstantsForRole(role))
    }

}
