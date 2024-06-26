package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(LocationComponent::class)
interface WaypointRepository : CrudRepository<WaypointEntity, Int>,
    EntityPageDataSource<WaypointEntity, Int> {

    override fun findAll(): List<WaypointEntity>
}
