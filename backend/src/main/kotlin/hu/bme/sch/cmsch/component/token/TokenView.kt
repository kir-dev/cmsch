package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class TokenView(
    @field:JsonView(FullDetails::class)
    val tokens: List<TokenDto>,

    @field:JsonView(FullDetails::class)
    val collectedTokenCount: Int,

    @field:JsonView(FullDetails::class)
    val totalTokenCount: Int,

    @field:JsonView(FullDetails::class)
    val minTokenToComplete: Int,
)
