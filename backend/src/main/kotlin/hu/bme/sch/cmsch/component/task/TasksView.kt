package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class TasksView(

    @field:JsonView(Preview::class)
    val categories: List<TaskCategoryDto> = listOf(),

)
