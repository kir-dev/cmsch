package hu.bme.sch.g7.dto.view

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.*
import hu.bme.sch.g7.model.UserEntity
import org.springframework.boot.context.properties.bind.Bindable.listOf

data class ProfileView(
        @JsonView(FullDetails::class)
        val loggedin: Boolean = false,

        @JsonView(FullDetails::class)
        val user: UserEntity,

        @JsonView(FullDetails::class)
        val group: GroupEntityDto? = null,

        @JsonView(FullDetails::class)
        val locations: List<GroupMemberLocationDto> = listOf(),

        @JsonView(FullDetails::class)
        val debts: List<DebtDto> = listOf()

)