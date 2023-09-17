package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(RiddleComponent::class)
interface RiddleEntityRepository : CrudRepository<RiddleEntity, Int>,
    EntityPageDataSource<RiddleEntity, Int> {
    
    override fun findAll(): List<RiddleEntity>

}
