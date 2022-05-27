package hu.bme.sch.cmsch.component.app

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ApplicationComponent::class)
interface ExtraMenuRepository : CrudRepository<ExtraMenuEntity, Int> {

}
