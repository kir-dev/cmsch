package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.component.event.EventEntity

data class SingleEventView(

    @JsonView(FullDetails::class)
    val event: EventEntity?

)
