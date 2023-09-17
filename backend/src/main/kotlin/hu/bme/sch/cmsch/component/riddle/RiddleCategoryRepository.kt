package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleComponent::class)
interface RiddleCategoryRepository : CrudRepository<RiddleCategoryEntity, Int>,
    EntityPageDataSource<RiddleCategoryEntity, Int> {

}
