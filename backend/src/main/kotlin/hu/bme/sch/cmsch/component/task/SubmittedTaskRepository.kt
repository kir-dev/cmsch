package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(TaskComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface SubmittedTaskRepository : CrudRepository<SubmittedTaskEntity, Int>,
    EntityPageDataSource<SubmittedTaskEntity, Int> {

    fun findByTask_IdAndGroupId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    fun findByTask_IdAndUserId(taskId: Int, groupId: Int): Optional<SubmittedTaskEntity>

    fun findByTask_Id(taskId: Int): List<SubmittedTaskEntity>

    fun findByTask_IdAndRejectedIsFalseAndApprovedIsFalse(taskId: Int): List<SubmittedTaskEntity>

    fun findAllByScoreGreaterThanAndApprovedIsTrue(zero: Int): List<SubmittedTaskEntity>

    fun findAllByGroupId(groupId: Int): List<SubmittedTaskEntity>

    fun findAllByUserId(userId: Int): List<SubmittedTaskEntity>

    fun countAllByUserIdAndRejectedFalseAndApprovedFalse(userId: Int): Int

    fun countAllByUserIdAndRejectedFalseAndApprovedTrue(userId: Int): Int

    fun findAllByUserIdAndTask_Id(userId: Int, taskId: Int): List<SubmittedTaskEntity>

}
