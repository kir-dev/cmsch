package hu.bme.sch.cmsch.dto.view

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
    var solved: Boolean = false

)
