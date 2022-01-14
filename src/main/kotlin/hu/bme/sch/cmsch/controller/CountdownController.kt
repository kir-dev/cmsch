package hu.bme.sch.cmsch.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CountdownController {

    @GetMapping("/countdown")
    fun countdown() = "countdown"

}
