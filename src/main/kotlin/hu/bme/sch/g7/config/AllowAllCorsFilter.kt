package hu.bme.sch.g7.config

import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class AllowAllCorsFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse?, filterChain: FilterChain) {
        if (servletResponse is HttpServletResponse) {
            val response = servletResponse
            val request = servletRequest as HttpServletRequest
            val requestOrigin = request.getHeader("Origin")
            response.setHeader("Access-Control-Allow-Origin", requestOrigin)
            response.setHeader("Access-Control-Allow-Credentials", "true")
            //response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD")
            response.setHeader("Access-Control-Max-Age", "3600")
            response.setHeader("Access-Control-Allow-Headers", "*")
            filterChain.doFilter(request, response)
        }
    }
}
