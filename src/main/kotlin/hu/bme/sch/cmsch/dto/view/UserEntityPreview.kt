package hu.bme.sch.cmsch.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType

class UserEntityPreview(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        val loggedin: Boolean = false,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val fullName: String? = null,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val groupName: String? = null,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val role: RoleType? = null
)
