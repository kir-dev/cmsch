package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

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
    fun testUser(auth: Authentication): String {
        log.info("test user endpoint was fired")
        return auth.getUserEntityFromDatabaseOrNull()?.fullName ?: "not logged in"
    }

}
