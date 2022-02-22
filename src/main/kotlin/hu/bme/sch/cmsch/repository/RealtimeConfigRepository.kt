package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.RealtimeConfigEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RealtimeConfigRepository : CrudRepository<RealtimeConfigEntity, Int> {
    fun findByKey(key: String): Optional<RealtimeConfigEntity>
}