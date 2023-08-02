package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TokenDto(

    @field:JsonView(FullDetails::class)
    val title: String,

    @field:JsonView(FullDetails::class)
    val type: String,

    @field:JsonView(FullDetails::class)
    val icon: String,

)
