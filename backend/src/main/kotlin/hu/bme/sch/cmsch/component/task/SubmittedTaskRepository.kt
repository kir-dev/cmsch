package hu.bme.sch.cmsch.component.task

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(TaskComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface SubmittedTaskRepository : CrudRepository<SubmittedTaskEntity, Int> {
    fun findByTask_IdAndGroupId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    fun findByTask_IdAndUserId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    fun findByTask_Id(taskId: Int): List<SubmittedTaskEntity>

    fun findByTask_IdAndRejectedIsFalseAndApprovedIsFalse(taskId: Int): List<SubmittedTaskEntity>

    fun findAllByScoreGreaterThanAndApprovedIsTrue(zero: Int): List<SubmittedTaskEntity>

    fun findAllByGroupId(groupId: Int): List<SubmittedTaskEntity>

    fun findAllByUserId(userId: Int): List<SubmittedTaskEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedFalse(userId: Int): List<SubmittedTaskEntity>

    fun findAllByUserIdAndRejectedFalseAndApprovedTrue(userId: Int): List<SubmittedTaskEntity>

    fun findAllByUserIdAndTask_Id(userId: Int, taskId: Int): List<SubmittedTaskEntity>
}
