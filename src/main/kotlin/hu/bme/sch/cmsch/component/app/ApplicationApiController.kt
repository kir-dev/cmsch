package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentHandlerService
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ApplicationComponent::class)
class ApplicationApiController(
    private val menuService: MenuService,
    private val componentHandlerService: ComponentHandlerService
) {

    @GetMapping("/app")
    fun app(request: HttpServletRequest): ApplicationConfigDto {
        val role = request.getUserOrNull()?.role ?: RoleType.GUEST
        return ApplicationConfigDto(
            role,
            menuService.getCachedMenuForRole(role),
            componentHandlerService.getComponentConstantsForRole(role))
    }

}
