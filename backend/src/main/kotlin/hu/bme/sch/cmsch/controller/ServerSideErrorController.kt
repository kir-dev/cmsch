package hu.bme.sch.cmsch.controller

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ServerSideErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(model: Model, request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        model.addAttribute("timestamp", System.currentTimeMillis())
        model.addAttribute("path", request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH))
        model.addAttribute("status", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        model.addAttribute("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
        model.addAttribute("trace", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION))

        if (status != null) {
            val statusCode = Integer.valueOf(status.toString())
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error-403"
            }
        }
        return "error"
    }

    @RequestMapping("/403")
    fun error403(): String {
        return "error-403"
    }

    @RequestMapping("/404")
    fun error404(): String {
        return "error"
    }

}
