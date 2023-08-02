package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

enum class RiddleSubmissionStatus {
    CORRECT,
    WRONG
}

data class RiddleSubmissionView(

    @field:JsonView(FullDetails::class)
    var status: RiddleSubmissionStatus = RiddleSubmissionStatus.WRONG,

    @field:JsonView(FullDetails::class)
    var nextId: Int? = null

)
