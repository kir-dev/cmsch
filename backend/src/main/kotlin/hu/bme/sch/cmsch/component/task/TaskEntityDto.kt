package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import java.io.Serializable

const val TWO_HOURS = 60 * 60 * 2

data class TaskEntityDto(

    @JsonView(FullDetails::class)
    val id: Int = 0,

    @JsonView(FullDetails::class)
    val title: String = "",

    @JsonView(FullDetails::class)
    val categoryId: Int = 0,

    @JsonView(FullDetails::class)
    val description: String = "",

    @JsonView(FullDetails::class)
    val expectedResultDescription: String = "",

    @JsonView(FullDetails::class)
    val type: TaskType = TaskType.TEXT,

    @JsonView(FullDetails::class)
    val format: TaskFormat = TaskFormat.NONE,

    @JsonView(FullDetails::class)
    val formatDescriptor: String = "",

    @JsonView(FullDetails::class)
    val availableFrom: Long = 0,

    @JsonView(FullDetails::class)
    val availableTo: Long = 0,

    @JsonView(FullDetails::class)
    val visible: Boolean = false

) : Serializable {

    constructor(other: TaskEntity, time: Long) : this(
        other.id,
        other.title,
        other.categoryId,

        if (time + TWO_HOURS > other.availableTo)
            (other.description + "\n\n" + (other.solution ?: ""))
        else
            other.description,

        other.expectedResultDescription,
        other.type,
        other.format,
        other.formatDescriptor,
        other.availableFrom,
        other.availableTo,
        other.visible
    )

}
