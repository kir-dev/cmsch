package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TokenView(
    @JsonView(FullDetails::class)
    val tokens: List<TokenDto>,

    @JsonView(FullDetails::class)
    val collectedTokenCount: Int,

    @JsonView(FullDetails::class)
    val totalTokenCount: Int,

    @JsonView(FullDetails::class)
    val minTokenToComplete: Int,
)
