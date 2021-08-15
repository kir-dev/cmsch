package hu.bme.sch.g7.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CountdownController {

    @GetMapping("/countdown")
    fun countdown() = "countdown"

}