package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.util.getUserOrNull
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ServerSideErrorController(private val adminMenuService: AdminMenuService) : ErrorController {

    @RequestMapping("/error")
    fun handleError(model: Model, auth: Authentication?, req: HttpServletRequest, res: HttpServletResponse): String {
        auth?.getUserOrNull()?.let {
            if (it.role.isStaff) {
                adminMenuService.addPartsForMenu(it, model)
                model.addAttribute("isStaff", true)

                val exception = req.getAttribute(RequestDispatcher.ERROR_EXCEPTION)
                val message = if (exception is Throwable) exception.stackTraceToString() else exception?.toString()
                model.addAttribute("exceptionMessage", message)
            }
        }

        model.addAttribute("status", res.status)
        return "error"
    }

    @RequestMapping("/403")
    fun error403(model: Model, auth: Authentication?, req: HttpServletRequest, res: HttpServletResponse): String {
        return handleError(model, auth, req, res)
    }

    @RequestMapping("/404")
    fun error404(model: Model, auth: Authentication?, req: HttpServletRequest, res: HttpServletResponse): String {
        return handleError(model, auth, req, res)
    }

}
