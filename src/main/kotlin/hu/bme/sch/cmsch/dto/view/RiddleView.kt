package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

class RiddleView(

    @JsonView(FullDetails::class)
    var imageUrl: String = "",

    @JsonView(FullDetails::class)
    var titleString: String = "",

    @JsonView(FullDetails::class)
    var hint: String? = null,

    @JsonView(FullDetails::class)
    var solved: Boolean = false

)
