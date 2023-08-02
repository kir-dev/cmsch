package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import java.io.Serializable

data class SubmittedTaskEntityDto(

    @field:JsonView(FullDetails::class)
    val id: Int = 0,

    @field:JsonView(FullDetails::class)
    val groupName: String = "",

    @field:JsonView(FullDetails::class)
    val userName: String = "",

    @field:JsonView(FullDetails::class)
    val textAnswer: String = "",

    @field:JsonView(FullDetails::class)
    val imageUrlAnswer: String = "",

    @field:JsonView(FullDetails::class)
    val fileUrlAnswer: String = "",

    @field:JsonView(FullDetails::class)
    val response: String = "",

    @field:JsonView(FullDetails::class)
    val approved: Boolean = false,

    @field:JsonView(FullDetails::class)
    val rejected: Boolean = false,

    @field:JsonView(FullDetails::class)
    val score: Int? = null

) : Serializable {

    constructor(other: SubmittedTaskEntity, showScore: Boolean) : this (
        other.id,
        other.groupName,
        other.userName,
        other.textAnswerLob,
        other.imageUrlAnswer,
        other.fileUrlAnswer,
        other.response,
        other.approved,
        other.rejected,
        if (showScore) other.score else null
    )

}
