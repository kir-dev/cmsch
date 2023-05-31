package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.service.TimeService
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class CountdownApiFilter(
    private val countdown: CountdownComponent,
    private val clock: TimeService
) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        val httpRequest = req as HttpServletRequest
        val httpResponse = res as HttpServletResponse

        if (httpRequest.servletPath.startsWith("/api/")
            && !httpRequest.servletPath.startsWith("/api/app")
            && countdown.isBlockedAt(clock.getTimeInSeconds())
        ) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Countdown is not finished")
            return
        }
        filterChain.doFilter(req, res)
    }

}
