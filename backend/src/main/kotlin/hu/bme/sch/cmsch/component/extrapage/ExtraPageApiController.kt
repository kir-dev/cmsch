package hu.bme.sch.cmsch.component.extrapage

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
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageApiController(
    private val extraPagesRepository: ExtraPageRepository,
    private val extraPageComponent: ExtraPageComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/page/{path}")
    @Operation(summary = "Detailed view of the selected extra page")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Details of the extra page requested"),
        ApiResponse(responseCode = "403", description = "This endpoint is not available for the given auth header"),
        ApiResponse(responseCode = "404", description = "No extra page found with this path")
    ])
    fun extraPage(@PathVariable path: String, auth: Authentication): ResponseEntity<ExtraPageView> {
        val user = auth.getUserOrNull()
        if (!extraPageComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val page = extraPagesRepository.findByUrlAndVisibleTrue(path).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        return if ((user?.role ?: RoleType.GUEST).value >= page.minRole.value) {
            ResponseEntity.ok(ExtraPageView(page = page))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

}
