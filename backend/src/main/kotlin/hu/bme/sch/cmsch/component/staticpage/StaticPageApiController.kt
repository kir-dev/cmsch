package hu.bme.sch.cmsch.component.staticpage

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(StaticPageComponent::class)
class StaticPageApiController(
    private val staticPageService: StaticPageService,
    private val staticPageComponent: StaticPageComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/page/{path}")
    @Operation(summary = "Detailed view of the selected extra page")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Details of the extra page requested"),
        ApiResponse(responseCode = "403", description = "This endpoint is not available for the given auth header"),
        ApiResponse(responseCode = "404", description = "No extra page found with this path")
    ])
    fun extraPage(@PathVariable path: String, auth: Authentication?): ResponseEntity<StaticPageView> {
        val user = auth?.getUserOrNull()
        if (!staticPageComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val page = staticPageService.fetchSpecificPage(path).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        return if ((user?.role ?: RoleType.GUEST).value >= page.minRole.value) {
            ResponseEntity.ok(StaticPageView(page = page))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

}
