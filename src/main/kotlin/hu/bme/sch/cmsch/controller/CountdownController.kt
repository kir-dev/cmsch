package hu.bme.sch.cmsch.controller

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@ConditionalOnProperty(prefix = "cmsch.component", name = ["countdown"], havingValue = "true", matchIfMissing = false)
@Controller
class CountdownController {

    @GetMapping("/countdown")
    fun countdown() = "countdown"

}
