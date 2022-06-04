package hu.bme.sch.cmsch.component.countdown

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["countdown"],
    havingValue = "true",
    matchIfMissing = false
)
class CountdownController {

    @GetMapping("/countdown")
    fun countdown() = "countdown"

}
