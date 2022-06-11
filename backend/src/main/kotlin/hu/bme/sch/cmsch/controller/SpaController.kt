package hu.bme.sch.cmsch.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SpaController {

    @GetMapping("/**/{path:[^.]*}")
    fun singlePageAppEndpoint(@PathVariable path: String) = "forward:/"

}
