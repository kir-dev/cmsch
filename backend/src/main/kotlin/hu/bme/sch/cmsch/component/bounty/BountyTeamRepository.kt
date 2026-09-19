package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(BountyComponent::class)
interface BountyTeamRepository : CrudRepository<BountyTeamEntity, Int>,
    EntityPageDataSource<BountyTeamEntity, Int> {

    override fun findAll(): MutableIterable<BountyTeamEntity>
    fun findAllByRoundId(roundId: Int): List<BountyTeamEntity>
    fun findAllByRoundIdAndEliminatedAtIsNull(roundId: Int): List<BountyTeamEntity>
    fun findByRoundIdAndGroupId(roundId: Int, groupId: Int): BountyTeamEntity?
}
