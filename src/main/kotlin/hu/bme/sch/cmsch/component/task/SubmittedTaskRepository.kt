package hu.bme.sch.cmsch.component.task

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(TaskComponent::class)
interface SubmittedTaskRepository : CrudRepository<SubmittedTaskEntity, Int> {
    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByTask_IdAndGroupId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByTask_IdAndUserId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByTask_Id(taskId: Int): List<SubmittedTaskEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByTask_IdAndRejectedIsFalseAndApprovedIsFalse(taskId: Int): List<SubmittedTaskEntity>

    fun findAllByScoreGreaterThanAndApprovedIsTrue(zero: Int): List<SubmittedTaskEntity>

    fun findAllByGroupId(groupId: Int): List<SubmittedTaskEntity>

    fun findAllByUserId(userId: Int): List<SubmittedTaskEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedFalse(userId: Int): List<SubmittedTaskEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedTrue(userId: Int): List<SubmittedTaskEntity>
}
