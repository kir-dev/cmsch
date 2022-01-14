package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonView

data class GroupStaffDto(
        @JsonView(FullDetails::class)
        val name: String,

        @JsonView(FullDetails::class)
        val facebookUrl: String,

        @JsonView(FullDetails::class)
        val mobilePhone: String
)
