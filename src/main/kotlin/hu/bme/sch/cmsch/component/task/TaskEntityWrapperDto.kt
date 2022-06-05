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

data class TaskEntityWrapperDto(
    @JsonView(value = [ Preview::class, FullDetails::class ])
    val task: TaskEntity,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    val status: TaskStatus,

    @JsonView(value = [ Preview::class, FullDetails::class ])
    val response: String
)
