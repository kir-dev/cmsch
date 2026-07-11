package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(SupportComponent::class)
interface SupportScheduleRepository : CrudRepository<SupportScheduleEntity, Int>, EntityPageDataSource<SupportScheduleEntity, Int>
