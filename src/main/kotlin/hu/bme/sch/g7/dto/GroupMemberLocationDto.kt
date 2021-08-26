package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView

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