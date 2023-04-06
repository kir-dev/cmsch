package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ApplicationComponent::class)
interface ExtraMenuRepository : CrudRepository<ExtraMenuEntity, Int>, EntityPageDataSource<ExtraMenuEntity, Int> {

}
