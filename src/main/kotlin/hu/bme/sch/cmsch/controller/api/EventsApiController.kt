package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dao.EventRepository
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.EventsView
import hu.bme.sch.cmsch.dto.view.SingleEventView
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class EventsApiController(
    private val config: RealtimeConfigService,
    private val eventsRepository: EventRepository,
) {

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(request: HttpServletRequest): EventsView {
        val user = request.getUserOrNull()
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        return EventsView(
            warningMessage = config.getWarningMessage(),
            allEvents = events
        )
    }

    @JsonView(FullDetails::class)
//    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String, request: HttpServletRequest): SingleEventView {
        val event = eventsRepository.findByUrl(path).orElse(null)

        return SingleEventView(
            event = event
        )
    }

}
