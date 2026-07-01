package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
@ConditionalOnBean(CommunitiesComponent::class)
interface TinderInteractionRepository : CrudRepository<TinderInteractionEntity, Int>, EntityPageDataSource<TinderInteractionEntity, Int> {

    fun findByCommunityId(communityId: Int): List<TinderInteractionEntity>

    fun findByUserId(userId: Int): List<TinderInteractionEntity>

    fun findByCommunityIdAndUserId(communityId: Int, userId: Int): Optional<TinderInteractionEntity>

}