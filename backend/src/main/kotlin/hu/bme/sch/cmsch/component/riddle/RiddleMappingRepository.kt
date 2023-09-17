package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int>,
    EntityPageDataSource<RiddleMappingEntity, Int> {

    fun findAllByOwnerUserId(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroupId(groupId: Int): List<RiddleMappingEntity>

    fun countAllByOwnerGroupIdAndCompletedTrue(groupId: Int): Int

    fun countAllByOwnerGroupIdAndCompletedTrueAndSkippedTrue(groupId: Int): Int


}
