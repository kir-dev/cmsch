package hu.bme.sch.g7.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control")
class AdminPanelCustomController {

    @GetMapping("/basics")
    fun dashboard(): String {
        return "admin"
    }

}