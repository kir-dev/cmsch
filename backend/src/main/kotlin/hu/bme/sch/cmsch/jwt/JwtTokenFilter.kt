package hu.bme.sch.cmsch.jwt

import hu.bme.sch.cmsch.service.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : GenericFilterBean() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        val httpRequest = req as HttpServletRequest
        if (httpRequest.servletPath.startsWith("/api/")) {
            val token: String? = jwtTokenProvider.resolveToken(httpRequest)
            if (token != null && jwtTokenProvider.validateToken(token)) {
                try {
                    val auth: Authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = auth
                } catch (e: Exception) {
                    log.warn("Invalid token: {} user cannot be resolved", token, e)
                }
            }
        }
        filterChain.doFilter(req, res)
    }

}
