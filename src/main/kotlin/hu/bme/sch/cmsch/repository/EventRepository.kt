package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.EventEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EventRepository : CrudRepository<EventEntity, Int> {
    override fun findAll(): List<EventEntity>
    fun findAllByVisibleTrueOrderByTimestampStart(): List<EventEntity>
    fun findByUrl(url: String): Optional<EventEntity>
}