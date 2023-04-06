package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(CommunitiesComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface CommunityRepository : CrudRepository<CommunityEntity, Int>,
    EntityPageDataSource<CommunityEntity, Int> {

    override fun findById(id: Int): Optional<CommunityEntity>

    override fun findAll(): List<CommunityEntity>

}
