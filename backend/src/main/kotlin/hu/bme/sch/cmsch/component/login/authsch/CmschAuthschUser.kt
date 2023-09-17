package hu.bme.sch.cmsch.component.login.authsch

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import java.io.Serializable
import java.security.Principal

class CmschAuthschUser(
    override val id: Int,
    override val internalId: String,
    override var role: RoleType,
    override var permissionsAsList: List<String>,
    override val userName: String,
    authorities: List<GrantedAuthority>,
    override val groupId: Int?,
    override val groupName: String
) : DefaultOAuth2User(authorities, mapOf("internal_id" to internalId), "internal_id"), CmschUser, Principal, Serializable {

    override fun getName() = internalId

    override fun hasPermission(permission: String): Boolean {
        return permissionsAsList.contains(permission)
    }

}
