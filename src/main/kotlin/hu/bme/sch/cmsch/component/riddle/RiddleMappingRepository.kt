package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(RiddleService::class)
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int> {
    fun findAllByOwnerUserAndCompletedTrue(user: UserEntity): List<RiddleMappingEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerUser_Id(userId: Int): List<RiddleMappingEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerUserAndRiddle_CategoryId(user: UserEntity, categoryId: Int): List<RiddleMappingEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByOwnerUserAndRiddle_Id(user: UserEntity, riddleId: Int): Optional<RiddleMappingEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByCompletedTrueAndRiddle_CategoryIdIn(categories: List<Int>): List<RiddleMappingEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByCompletedTrueAndOwnerUserAndRiddle_CategoryIdIn(user: UserEntity, categories: List<Int>): List<RiddleMappingEntity>
}
