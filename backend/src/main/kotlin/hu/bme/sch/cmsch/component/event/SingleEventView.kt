package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class SingleEventView(

    @field:JsonView(FullDetails::class)
    val event: EventEntity?

)
