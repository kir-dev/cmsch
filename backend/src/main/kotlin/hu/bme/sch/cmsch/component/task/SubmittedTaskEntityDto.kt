package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import java.io.Serializable

data class SubmittedTaskEntityDto(

    @JsonView(FullDetails::class)
    val id: Int = 0,

    @JsonView(FullDetails::class)
    val groupName: String = "",

    @JsonView(FullDetails::class)
    val userName: String = "",

    @JsonView(FullDetails::class)
    val textAnswer: String = "",

    @JsonView(FullDetails::class)
    val imageUrlAnswer: String = "",

    @JsonView(FullDetails::class)
    val fileUrlAnswer: String = "",

    @JsonView(FullDetails::class)
    val response: String = "",

    @JsonView(FullDetails::class)
    val approved: Boolean = false,

    @JsonView(FullDetails::class)
    val rejected: Boolean = false,

    @JsonView(FullDetails::class)
    val score: Int? = null

) : Serializable {

    constructor(other: SubmittedTaskEntity, showScore: Boolean) : this (
        other.id,
        other.groupName,
        other.userName,
        if (other.textAnswerLob?.isNotBlank() == true) (other.textAnswerLob ?: other.textAnswer) else other.textAnswer,
        other.imageUrlAnswer,
        other.fileUrlAnswer,
        other.response,
        other.approved,
        other.rejected,
        if (showScore) other.score else null
    )

}
