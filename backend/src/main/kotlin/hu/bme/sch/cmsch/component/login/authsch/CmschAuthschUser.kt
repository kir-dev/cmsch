package hu.bme.sch.cmsch.component.login.authsch

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import java.io.Serializable
import java.security.Principal

class CmschAuthschUser(
    private val oidcUser: OidcUser,
    override val id: Int,
    override val internalId: String,
    override var role: RoleType,
    override var permissionsAsList: List<String>,
    override val userName: String,
    override val groupId: Int?,
    override val groupName: String,
) : OidcUser by oidcUser, CmschUser, Principal, Serializable {

    companion object {
        private const val serialVersionUID: Long = 1L
    }

    override fun getName() = internalId

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun hasPermission(permission: String): Boolean {
        return permissionsAsList.contains(permission)
    }

}
