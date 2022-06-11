package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleService::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int> {
    fun findAllByOwnerUser_IdAndCompletedTrue(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerUser_Id(userId: Int): List<RiddleMappingEntity>

    fun findAllByOwnerUser_IdAndRiddle_CategoryId(userId: Int, categoryId: Int): List<RiddleMappingEntity>

    fun findByOwnerUser_IdAndRiddle_Id(userId: Int, riddleId: Int): Optional<RiddleMappingEntity>

    fun findAllByCompletedTrueAndRiddle_CategoryIdIn(categories: List<Int>): List<RiddleMappingEntity>

    fun findAllByCompletedTrueAndOwnerUserAndRiddle_CategoryIdIn(user: UserEntity, categories: List<Int>): List<RiddleMappingEntity>
}
