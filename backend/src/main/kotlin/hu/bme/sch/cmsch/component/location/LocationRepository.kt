package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(LocationComponent::class)
interface LocationRepository : CrudRepository<LocationEntity, Int>,
    EntityPageDataSource<LocationEntity, Int> {

    fun findByUserId(userId: Int): Optional<LocationEntity>
    override fun findAll(): List<LocationEntity>
}
