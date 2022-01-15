package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

enum class RiddleSubmissionStatus {
    CORRECT,
    WRONG
}

class RiddleSubmissionView(

    @JsonView(FullDetails::class)
    var status: RiddleSubmissionStatus = RiddleSubmissionStatus.WRONG,

    @JsonView(FullDetails::class)
    var nextId: Int? = null

)
