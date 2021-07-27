package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.model.EventEntity

data class SingleEventView(
    @JsonView(FullDetails::class)
    val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?

    @JsonView(FullDetails::class)
    val event: EventEntity?
)