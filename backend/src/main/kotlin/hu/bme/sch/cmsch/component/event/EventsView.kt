package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview

data class EventsView(

    @field:JsonView(Preview::class)
    val allEvents: List<EventEntity> = listOf(),

)
