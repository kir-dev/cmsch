package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
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
@ConditionalOnBean(EventComponent::class)
class EventApiController(
    private val eventComponent: EventComponent,
    private val eventService: EventService
) {

    @JsonView(Preview::class)
    @GetMapping("/events")
    @Operation(
        summary = "List all the available events for the authenticated user.",
        description = "If no authenticated user found, it will show events available for GUEST role. " +
                "The 'allEvents' field might be empty."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of events available"),
        ApiResponse(responseCode = "403", description = "This endpoint is not available for the given auth header")
    ])
    fun events(auth: Authentication?): ResponseEntity<EventsView> {
        val user = auth?.getUserOrNull()
        if (!eventComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val events = eventService.fetchEvents(user)

        return ResponseEntity.ok(EventsView(allEvents = events))
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    @Operation(summary = "Detailed view of the selected event")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Details of the event requested"),
        ApiResponse(responseCode = "403", description = "This endpoint is not available for the given auth header " +
                "or 'eventComponent.enableDetailedView' is false"),
        ApiResponse(responseCode = "404", description = "No events found with this path")
    ])
    fun event(@PathVariable path: String, auth: Authentication?): ResponseEntity<SingleEventView> {
        if (!eventComponent.enableDetailedView.isValueTrue())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val user = auth?.getUserOrNull()
        if (!eventComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val event = eventService.getSpecificEvent(path).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        return if ((user?.role ?: RoleType.GUEST).value >= event.minRole.value) {
            ResponseEntity.ok(SingleEventView(event = event))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

}
