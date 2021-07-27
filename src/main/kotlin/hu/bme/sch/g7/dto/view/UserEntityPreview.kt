package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview

@JsonView(value = [ Preview::class, FullDetails::class ])
class UserEntityPreview(
        val loggedin: Boolean = false,
        val fullName: String? = null,
        val groupName: String? = null,
)