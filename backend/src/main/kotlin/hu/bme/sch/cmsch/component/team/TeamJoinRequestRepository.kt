package hu.bme.sch.cmsch.component.team

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TeamComponent::class)
interface TeamJoinRequestRepository : CrudRepository<TeamJoinRequestEntity, Int> {
    fun existsByUserIdAndGroupId(userId: Int, groupId: Int): Boolean
    fun findAllByGroupId(groupId: Int): List<TeamJoinRequestEntity>
    fun deleteAllByUserId(userId: Int)
    fun existsByUserId(userId: Int): Boolean
}
