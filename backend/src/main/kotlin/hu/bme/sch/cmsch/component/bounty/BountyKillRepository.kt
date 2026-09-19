package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(BountyComponent::class)
interface BountyKillRepository : CrudRepository<BountyKillEntity, Int>,
    EntityPageDataSource<BountyKillEntity, Int> {

    override fun findAll(): MutableIterable<BountyKillEntity>
    fun findAllByRoundId(roundId: Int): List<BountyKillEntity>
    fun findAllByRoundIdAndKillerGroupId(roundId: Int, groupId: Int): List<BountyKillEntity>
}
