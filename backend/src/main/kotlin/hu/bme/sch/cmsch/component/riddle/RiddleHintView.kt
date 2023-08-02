package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class RiddleHintView(

    @field:JsonView(FullDetails::class)
    var hint: String = ""

)
