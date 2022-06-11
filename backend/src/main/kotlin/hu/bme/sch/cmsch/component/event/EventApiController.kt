package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(EventComponent::class)
class EventApiController(
    private val eventsRepository: EventRepository,
    private val eventComponent: EventComponent
) {

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(auth: Authentication): EventsView {
        val user = auth.getUserOrNull()
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        return EventsView(
            allEvents = events
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String, request: HttpServletRequest): SingleEventView {
        if (!eventComponent.enableDetailedView.isValueTrue())
            return SingleEventView(event = EventEntity(id = 0, title = "detailed view disabled"))

        val event = eventsRepository.findByUrl(path).orElse(null)
        return SingleEventView(event = event)
    }

}
