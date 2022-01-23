package hu.bme.sch.cmsch.controller

import org.springframework.web.bind.annotation.RequestMapping

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus

import javax.servlet.RequestDispatcher




@Controller
class ServerSideErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        if (status != null) {
            val statusCode = Integer.valueOf(status.toString())
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error-403"
            }
        }
        return "error"
    }

}