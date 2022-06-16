package hu.bme.sch.cmsch.component.event

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(EventComponent::class)
interface EventRepository : CrudRepository<EventEntity, Int> {
    override fun findAll(): List<EventEntity>
    fun findByUrl(url: String): Optional<EventEntity>
    fun findAllByVisibleTrueOrderByTimestampStart(): List<EventEntity>
}
