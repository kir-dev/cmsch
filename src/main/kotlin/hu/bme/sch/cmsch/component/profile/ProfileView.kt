package hu.bme.sch.cmsch.component.profile

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.*
import hu.bme.sch.cmsch.model.UserEntity

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
