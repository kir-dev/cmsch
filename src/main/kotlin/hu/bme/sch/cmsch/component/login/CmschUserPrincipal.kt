package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.model.RoleType
import java.io.Serializable
import java.security.Principal

data class CmschUserPrincipal(
    override val id: Int,
    override val internalId: String,
    override val role: RoleType,
    val permissions: List<String>
) : Serializable, Principal, CmschUser {

    override fun getName() = internalId

    override fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission)
    }

}
