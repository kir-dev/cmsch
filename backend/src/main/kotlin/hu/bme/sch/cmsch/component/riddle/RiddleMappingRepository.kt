package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(RiddleComponent::class)
@Suppress("FunctionName", "kotlin:S100")
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int>,
    EntityPageDataSource<RiddleMappingEntity, Int> {

    fun findAllByOwnerUserId(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroupId(groupId: Int): List<RiddleMappingEntity>

    fun countAllByOwnerGroupIdAndCompletedTrue(groupId: Int): Int

    fun countAllByOwnerGroupIdAndCompletedTrueAndSkippedTrue(groupId: Int): Int

    fun findAllByOwnerUserIdAndCompletedTrue(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroupIdAndCompletedTrue(groupId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerUserIdAndRiddleId(userId: Int, riddleId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroupIdAndRiddleId(groupId: Int, riddleId: Int): List<RiddleMappingEntity>

    fun countAllByCompletedTrueAndRiddleIdIn(riddleIds: List<Int>): Int

    fun countAllByCompletedTrueAndOwnerUserIdAndRiddleIdIn(userId: Int, riddleIds: List<Int>): Int

    fun countAllByCompletedTrueAndOwnerGroupIdAndRiddleIdIn(groupId: Int, riddleIds: List<Int>): Int

    fun countAllByCompletedTrueAndSkippedFalseAndRiddleId(riddleId: Int): Int

}
