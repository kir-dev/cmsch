package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class GroupMemberLocationDto(
    @field:JsonView(FullDetails::class)
    val name: String,

    @field:JsonView(FullDetails::class)
    val longitude: Double,

    @field:JsonView(FullDetails::class)
    val latitude: Double,

    @field:JsonView(FullDetails::class)
    val accuracy: Float,

    @field:JsonView(FullDetails::class)
    val timestamp: Long
)
