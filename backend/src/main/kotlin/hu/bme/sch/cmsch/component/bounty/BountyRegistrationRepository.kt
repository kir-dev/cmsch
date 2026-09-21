package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(BountyComponent::class)
interface BountyRegistrationRepository : CrudRepository<BountyRegistrationEntity, Int>,
    EntityPageDataSource<BountyRegistrationEntity, Int> {

    override fun findAll(): MutableIterable<BountyRegistrationEntity>
    fun findAllByRoundId(roundId: Int): List<BountyRegistrationEntity>
    fun findAllByRoundIdAndAliveTrue(roundId: Int): List<BountyRegistrationEntity>
    fun findByRoundIdAndUserId(roundId: Int, userId: Int): BountyRegistrationEntity?
    fun findByCode(code: String): BountyRegistrationEntity?
    fun countByRoundIdAndGroupIdAndAliveTrue(roundId: Int, groupId: Int): Int
    fun findAllByRoundIdAndAliveTrueAndGroupId(roundId: Int, groupId: Int): List<BountyRegistrationEntity>
}
