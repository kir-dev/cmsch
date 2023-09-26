package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import java.io.Serializable

const val TWO_HOURS = 60 * 60 * 2

data class TaskEntityDto(

    @field:JsonView(FullDetails::class)
    val id: Int = 0,

    @field:JsonView(FullDetails::class)
    val title: String = "",

    @field:JsonView(FullDetails::class)
    val categoryId: Int = 0,

    @field:JsonView(FullDetails::class)
    val description: String = "",

    @field:JsonView(FullDetails::class)
    val expectedResultDescription: String = "",

    @field:JsonView(FullDetails::class)
    val type: TaskType = TaskType.TEXT,

    @field:JsonView(FullDetails::class)
    val format: TaskFormat = TaskFormat.NONE,

    @field:JsonView(FullDetails::class)
    val formatDescriptor: String = "",

    @field:JsonView(FullDetails::class)
    val availableFrom: Long = 0,

    @field:JsonView(FullDetails::class)
    val availableTo: Long = 0,

    @field:JsonView(FullDetails::class)
    val visible: Boolean = false,

    @field:JsonView(FullDetails::class)
    val categoryName: String = "",

) : Serializable {

    constructor(other: TaskEntity, time: Long, categoryName: String?) : this(
        other.id,
        other.title,
        other.categoryId,

        if (time - TWO_HOURS > other.availableTo)
            (other.description + "\n\n" + other.solution)
        else
            other.description,

        other.expectedResultDescription,
        other.type,
        other.format,
        other.formatDescriptor,
        other.availableFrom,
        other.availableTo,
        other.visible,
        categoryName ?: ""
    )

}
