package hu.bme.sch.cmsch.component.login.google

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import java.io.Serializable
import java.security.Principal

class CmschGoogleUser(
    override val id: Int,
    override val internalId: String,
    override val role: RoleType,
    override val permissionsAsList: List<String>,
    override val userName: String,
    authorities: List<GrantedAuthority>,
    idToken: OidcIdToken,
) : DefaultOidcUser(authorities, idToken), CmschUser, Principal, Serializable {

    override fun getName() = internalId

    override fun hasPermission(permission: String): Boolean {
        return permissionsAsList.contains(permission)
    }

}