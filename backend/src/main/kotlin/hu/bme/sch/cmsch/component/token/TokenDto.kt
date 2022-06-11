package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TokenDto(

    @JsonView(FullDetails::class)
    val title: String,

    @JsonView(FullDetails::class)
    val type: String

)
