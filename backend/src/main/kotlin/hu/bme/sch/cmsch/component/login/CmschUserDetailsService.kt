package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

class CmschUserDetails(
    val userEntity: UserEntity,
    private val loginComponent: LoginComponent
) : UserDetails, CmschUser by userEntity {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${userEntity.role.name}"))
    }

    override fun getPassword(): String? = userEntity.password

    override fun getUsername(): String = userEntity.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean {
        return if (loginComponent.emailConfirmationEnabled && userEntity.provider == "password") {
            userEntity.emailConfirmed
        } else {
            true
        }
    }
}

@Service
class CmschUserDetailsService(
    private val userRepository: UserRepository,
    private val loginComponent: LoginComponent
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("User not found with email: $username") }
        return CmschUserDetails(user, loginComponent)
    }
}
