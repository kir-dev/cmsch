package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int> {
    fun findAllByOwnerUser_IdAndCompletedTrue(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroup_IdAndCompletedTrue(groupId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerUser_Id(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroup_Id(groupId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerUser_IdAndRiddle_CategoryId(userId: Int, categoryId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerGroup_IdAndRiddle_CategoryId(groupId: Int, categoryId: Int): List<RiddleMappingEntity>

    fun findByOwnerUser_IdAndRiddle_Id(userId: Int, riddleId: Int): Optional<RiddleMappingEntity>

    fun findByOwnerGroup_IdAndRiddle_Id(groupId: Int, riddleId: Int): Optional<RiddleMappingEntity>

    fun findAllByCompletedTrueAndRiddle_CategoryIdIn(categories: List<Int>): List<RiddleMappingEntity>

    fun findAllByCompletedTrueAndOwnerUserAndRiddle_CategoryIdIn(user: UserEntity, categories: List<Int>): List<RiddleMappingEntity>

    fun findAllByCompletedTrueAndOwnerGroupAndRiddle_CategoryIdIn(group: GroupEntity, categories: List<Int>): List<RiddleMappingEntity>
}
