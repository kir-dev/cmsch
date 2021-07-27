package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview

class UserEntityPreview(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        val loggedin: Boolean = false,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val fullName: String? = null,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val groupName: String? = null
)