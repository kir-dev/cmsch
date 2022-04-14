package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["test"],
    havingValue = "true",
    matchIfMissing = false
)
@Controller
class TestController {

    private val log = LoggerFactory.getLogger(javaClass)

    @ResponseBody
    @GetMapping("/control/test")
    fun test(): String {
        log.info("test endpoint was fired")
        return "Pong!"
    }

    @ResponseBody
    @GetMapping("/control/test-user")
    fun testUser(request: HttpServletRequest): String {
        log.info("test user endpoint was fired")
        return request.getUserOrNull()?.fullName ?: "not logged in"
    }

}
