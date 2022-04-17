package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class GroupMemberLocationDto(
    @JsonView(FullDetails::class)
    val name: String,

    @JsonView(FullDetails::class)
    val logitude: Double,

    @JsonView(FullDetails::class)
    val latitude: Double,

    @JsonView(FullDetails::class)
    val accuracy: Float,

    @JsonView(FullDetails::class)
    val timestamp: Long
)
