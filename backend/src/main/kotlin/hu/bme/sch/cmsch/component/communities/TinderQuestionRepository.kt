package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
@ConditionalOnBean(CommunitiesComponent::class)
interface TinderQuestionRepository : CrudRepository<TinderQuestionEntity, Int>, EntityPageDataSource<TinderQuestionEntity, Int>
