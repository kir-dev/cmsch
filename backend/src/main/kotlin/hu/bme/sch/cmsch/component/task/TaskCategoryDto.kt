package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class TaskCategoryDto(
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var name: String = "",

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var categoryId: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var availableFrom: Long = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var availableTo: Long = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var sum: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var approved: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var rejected: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var notGraded: Int = 0,

    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    var type: TaskCategoryType = TaskCategoryType.REGULAR,

)
