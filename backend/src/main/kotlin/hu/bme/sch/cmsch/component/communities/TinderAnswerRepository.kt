package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
@ConditionalOnBean(CommunitiesComponent::class)
interface TinderAnswerRepository : CrudRepository<TinderAnswerEntity, Int>, EntityPageDataSource<TinderAnswerEntity, Int> {

    fun findByCommunityId(communityId: Int): Optional<TinderAnswerEntity>
    fun findByUserId(userId: Int): Optional<TinderAnswerEntity>

    @Query("select t from TinderAnswerEntity t where t.communityId is not null")
    fun findAllWithCommunityIdNotNull(): MutableList<TinderAnswerEntity>

    @Query("select t from TinderAnswerEntity t where t.userId is not null")
    fun findAllWithUserIdNotNull(): MutableList<TinderAnswerEntity>

}