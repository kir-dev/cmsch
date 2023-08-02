package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

enum class TaskStatus {
    NOT_SUBMITTED,
    SUBMITTED,
    REJECTED,
    ACCEPTED,
    NOT_LOGGED_IN
}

fun resolveTaskStatus(submission: SubmittedTaskEntity?) =
    if (submission?.approved == true) TaskStatus.ACCEPTED
    else if (submission?.rejected == true) TaskStatus.REJECTED
    else if (submission?.approved == false && !submission.rejected) TaskStatus.SUBMITTED
    else TaskStatus.NOT_SUBMITTED

data class TaskEntityWrapperDto(
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    val task: TaskEntity,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    val status: TaskStatus,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    val response: String
)
