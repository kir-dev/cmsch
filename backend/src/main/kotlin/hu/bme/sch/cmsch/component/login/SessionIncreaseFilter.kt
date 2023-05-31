package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest

class SessionIncreaseFilter(
    private val startupPropertyConfig: StartupPropertyConfig
) : GenericFilterBean() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        if (request.servletPath.startsWith("/admin/")) {
            val session = request.getSession(true)
            val user = SecurityContextHolder.getContext()?.authentication?.getUserOrNull()
            if (user?.let { ControlPermissions.PERMISSION_INCREASED_SESSION_DURATION.validate(it) } == true) {
                log.debug(
                    "Increasing session time for user {} is {}",
                    user.userName,
                    startupPropertyConfig.increasedSessionTime
                )
                session.maxInactiveInterval = startupPropertyConfig.increasedSessionTime
            }
        }

        filterChain.doFilter(servletRequest, response)
    }

}
