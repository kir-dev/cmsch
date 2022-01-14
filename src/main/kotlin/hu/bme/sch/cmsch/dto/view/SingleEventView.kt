package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.EventEntity

data class SingleEventView(
        @JsonView(FullDetails::class)
        val warningMessage: String = "",

        @JsonView(FullDetails::class)
        val event: EventEntity?
)
