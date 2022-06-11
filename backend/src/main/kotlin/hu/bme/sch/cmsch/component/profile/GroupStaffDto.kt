package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class GroupStaffDto(
    @JsonView(FullDetails::class)
    val name: String,

    @JsonView(FullDetails::class)
    val facebookUrl: String,

    @JsonView(FullDetails::class)
    val mobilePhone: String
)
