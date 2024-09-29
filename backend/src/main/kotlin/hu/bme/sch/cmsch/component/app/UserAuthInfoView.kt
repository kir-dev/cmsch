package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType

enum class AuthState {
    EXPIRED,
    LOGGED_IN,
    LOGGED_OUT
}


data class UserAuthInfoView(
    @field:JsonView(FullDetails::class)
    val authState: AuthState = AuthState.LOGGED_OUT,

    @field:JsonView(FullDetails::class)
    val id: Int? = null,

    @field:JsonView(FullDetails::class)
    val internalId: String? = null,

    @field:JsonView(FullDetails::class)
    val role: RoleType? = null,

    @field:JsonView(FullDetails::class)
    val permissionsAsList: List<String>? = null,

    @field:JsonView(FullDetails::class)
    val userName: String? = null,

    @field:JsonView(FullDetails::class)
    val groupId: Int? = null,

    @field:JsonView(FullDetails::class)
    val groupName: String? = null,
)
