package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TaskCategoryView(
    @JsonView(FullDetails::class)
    val categoryName: String,

    @JsonView(FullDetails::class)
    val tasks: List<TaskEntityWrapperDto> = listOf(),

    @JsonView(FullDetails::class)
    val availableFrom: Long = 0,

    @JsonView(FullDetails::class)
    val availableTo: Long = 0,

    @JsonView(FullDetails::class)
    val type: TaskCategoryType = TaskCategoryType.REGULAR
)
