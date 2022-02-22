package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.LocationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LocationRepository : CrudRepository<LocationEntity, Int> {
    fun findByUserId(userId: Int): Optional<LocationEntity>
    override fun findAll(): List<LocationEntity>
}