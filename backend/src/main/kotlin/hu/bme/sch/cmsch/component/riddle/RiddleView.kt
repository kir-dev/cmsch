package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data  class RiddleView(

    @JsonView(FullDetails::class)
    var imageUrl: String = "",

    @JsonView(FullDetails::class)
    var title: String = "",

    @JsonView(FullDetails::class)
    var hint: String? = null,

    @JsonView(FullDetails::class)
    var solved: Boolean = false,

    @JsonView(FullDetails::class)
    var creator: String? = null,

    @JsonView(FullDetails::class)
    var firstSolver: String? = null,

)
