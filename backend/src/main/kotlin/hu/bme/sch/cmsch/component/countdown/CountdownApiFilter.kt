package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserOrNull
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class CountdownApiFilter(
    private val countdown: CountdownComponent,
    private val clock: TimeService
) : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {
        if (req.servletPath.startsWith("/api/") && !req.servletPath.startsWith("/api/app")) {
            val cmschUser = SecurityContextHolder.getContext()?.authentication?.getUserOrNull()
            val role = cmschUser?.role ?: RoleType.GUEST
            if (countdown.isBlockedAt(clock.getTimeInSeconds(), role)) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Countdown is not finished")
                return
            }
        }
        filterChain.doFilter(req, res)
    }

}
