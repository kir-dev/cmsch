package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.component.login.CmschUserPrincipal
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class ServiceAccountFilter(
    private val serviceAccountKeyRepository: ServiceAccountKeyRepository,
    private val clock: TimeService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(ServiceAccountFilter::class.java)

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        readAndUseServiceAccountFromRequest(req)
        chain.doFilter(req, res)
    }

    private fun readAndUseServiceAccountFromRequest(request: HttpServletRequest) {
        val secretToken = request.getHeader("x-cmsch-service-account-key")
        if (secretToken.isNullOrBlank()) return

        val now = clock.getTimeInSeconds()
        val user = serviceAccountKeyRepository.findUserByValidServiceAccountKey(secretToken, now)
        if (user == null) {
            log.error("User used in service account doesn't exist: {}", secretToken)
            return
        }

        if (!user.isServiceAccount) {
            log.error("User used in service account key [{}] is not a service account: {}", secretToken, user)
            return
        }
        SecurityContextHolder.getContext().authentication = getAuthenticationFromServiceAccount(user)
    }

    private fun getAuthenticationFromServiceAccount(user: UserEntity): Authentication {
        return UsernamePasswordAuthenticationToken(
            CmschUserPrincipal(
                id = user.id,
                internalId = user.internalId,
                role = user.role,
                permissionsAsList = user.permissionsAsList,
                userName = user.fullName,
                groupId = user.groupId,
                groupName = user.groupName
            ),
            "",
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        )
    }
}
