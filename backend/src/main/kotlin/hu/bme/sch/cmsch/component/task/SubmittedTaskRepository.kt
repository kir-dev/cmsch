package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class SubmissionSummary(
    var categoryId: Int = 0,
    var approved: Long = 0,
    var rejected: Long = 0,
    var notGraded: Long = 0
)

data class TaskAggregateDto(
    var taskId: Int = 0,
    var taskTitle: String = "n/a",
    var approvedCount: Long = 0,
    var rejectedCount: Long = 0,
    var notGradedCount: Long = 0
)

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

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.task.SubmissionSummary(
            s.categoryId,
            SUM(CASE WHEN s.approved THEN 1 ELSE 0 END),
            SUM(CASE WHEN s.rejected THEN 1 ELSE 0 END),
            SUM(CASE WHEN s.approved = false AND s.rejected = false THEN 1 ELSE 0 END)
        ) 
        FROM SubmittedTaskEntity s 
        WHERE s.groupId = :groupId 
        GROUP BY s.categoryId
    """)
    fun findSubmissionSummaryByGroupId(groupId: Int): List<SubmissionSummary>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.task.SubmissionSummary(
            s.categoryId,
            SUM(CASE WHEN s.approved THEN 1 ELSE 0 END), 
            SUM(CASE WHEN s.rejected THEN 1 ELSE 0 END), 
            SUM(CASE WHEN s.approved = false AND s.rejected = false THEN 1 ELSE 0 END)
        ) 
        FROM SubmittedTaskEntity s 
        WHERE s.userId = :userId 
        GROUP BY s.categoryId
    """)
    fun findSubmissionSummaryByUserId(userId: Int): List<SubmissionSummary>

    fun findAllByGroupId(groupId: Int): List<SubmittedTaskEntity>

    fun findAllByUserId(userId: Int): List<SubmittedTaskEntity>

    fun countAllByUserIdAndRejectedFalseAndApprovedFalse(userId: Int): Int

    fun countAllByUserIdAndRejectedFalseAndApprovedTrue(userId: Int): Int

    fun findAllByUserIdAndTask_Id(userId: Int, taskId: Int): List<SubmittedTaskEntity>

    fun findAllByTask_IdInAndGroupId(taskIds: List<Int>, groupId: Int): List<SubmittedTaskEntity>

    fun findAllByTask_IdInAndUserId(taskIds: List<Int>, userId: Int): List<SubmittedTaskEntity>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.task.TaskAggregateDto(
            s.task.id, 
            s.task.title,
            SUM(CASE WHEN s.approved = TRUE THEN 1 ELSE 0 END),
            SUM(CASE WHEN s.rejected = TRUE THEN 1 ELSE 0 END),
            SUM(CASE WHEN s.approved = FALSE AND s.rejected = FALSE THEN 1 ELSE 0 END)
        )
        FROM SubmittedTaskEntity s
        GROUP BY s.task.id, s.task.title
    """)
    fun findAllAggregated(): List<TaskAggregateDto>

    @Query("""
        SELECT NEW hu.bme.sch.cmsch.component.task.SubmittedTaskEntity(
            s.id,
            s.task,
            s.groupId,
            s.groupName,
            s.userId,
            s.userName,
            s.categoryId,
            '',
            s.imageUrlAnswer,
            s.fileUrlAnswer,
            s.response,
            s.approved,
            s.rejected,
            s.score,
            ''
        )
        FROM SubmittedTaskEntity s
        WHERE s.task.id = :taskId AND s.rejected = FALSE AND s.approved = FALSE
    """)
    fun findByTask_IdAndRejectedIsFalseAndApprovedIsFalseWithoutLobs(taskId: Int): List<SubmittedTaskEntity>

}
