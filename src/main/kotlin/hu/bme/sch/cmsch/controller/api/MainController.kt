package hu.bme.sch.cmsch.controller.api

import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.ClockService
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat

val UNKNOWN_USER = UserEntity(0, fullName = "Feature Not Available")

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class MainController(
    private val clock: ClockService
) {

    private val formatter = SimpleDateFormat("yyyy.MM.dd. HH:mm:ss")

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "2.6.5"

    @ResponseBody
    @GetMapping("/time")
    fun time(): String = formatter.format(clock.getTimeInSeconds())

}
