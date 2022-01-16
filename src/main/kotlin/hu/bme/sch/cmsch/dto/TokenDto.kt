package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView

data class TokenDto(

    @JsonView(FullDetails::class)
    val title: String,

    @JsonView(FullDetails::class)
    val type: String

)
