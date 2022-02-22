package hu.bme.sch.cmsch.riddle.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

enum class RiddleSubmissionStatus {
    CORRECT,
    WRONG
}

data class RiddleSubmissionView(

    @JsonView(FullDetails::class)
    var status: RiddleSubmissionStatus = RiddleSubmissionStatus.WRONG,

    @JsonView(FullDetails::class)
    var nextId: Int? = null

)
