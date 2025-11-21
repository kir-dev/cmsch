package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.test"])
@Controller
class TestController(private val userService: UserService) {

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
        return auth.getUserEntityFromDatabaseOrNull(userService)?.fullName ?: "not logged in"
    }

}
