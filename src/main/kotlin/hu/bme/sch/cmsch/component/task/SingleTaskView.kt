package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class SingleTaskView(

    @JsonView(FullDetails::class)
    // If null: task not found
    val task: TaskEntity? = null,

    @JsonView(FullDetails::class)
    // If null: no submission
    val submission: SubmittedTaskEntity? = null,

    @JsonView(FullDetails::class)
    val status: TaskStatus = TaskStatus.NOT_LOGGED_IN

)
