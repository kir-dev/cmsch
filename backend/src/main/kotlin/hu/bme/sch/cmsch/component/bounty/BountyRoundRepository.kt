package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(BountyComponent::class)
interface BountyRoundRepository : CrudRepository<BountyRoundEntity, Int>,
    EntityPageDataSource<BountyRoundEntity, Int> {

    override fun findAll(): MutableIterable<BountyRoundEntity>
    fun findAllByOrderByGameStartAsc(): List<BountyRoundEntity>
}
