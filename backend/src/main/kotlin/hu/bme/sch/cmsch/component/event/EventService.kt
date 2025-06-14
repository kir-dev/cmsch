package hu.bme.sch.cmsch.component.event

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(EventComponent::class)
class EventService(
    private val eventsRepository: EventRepository
) {

    @Transactional(readOnly = true)
    fun fetchEvents(user: CmschUser?) =
        eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

    @Transactional(readOnly = true)
    fun getSpecificEvent(path: String) = eventsRepository.findByUrl(path)

}
