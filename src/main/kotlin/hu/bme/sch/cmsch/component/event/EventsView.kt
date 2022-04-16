package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.component.event.EventEntity

data class EventsView(

    @JsonView(Preview::class)
    val allEvents: List<EventEntity> = listOf(),

)
