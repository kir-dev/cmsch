package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.GroupEntityDto
import hu.bme.sch.g7.dto.GroupMemberLocationDto
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.model.UserEntity
import org.springframework.boot.context.properties.bind.Bindable.listOf

data class ProfileView(
        @JsonView(FullDetails::class)
        val warningMessage: String = "",

        @JsonView(FullDetails::class)
        val user: UserEntity,

        @JsonView(FullDetails::class)
        val group: GroupEntityDto? = null,

        @JsonView(FullDetails::class)
        val locations: List<GroupMemberLocationDto> = listOf()

)