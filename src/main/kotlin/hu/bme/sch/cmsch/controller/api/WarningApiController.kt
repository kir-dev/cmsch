package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.WarningView
import hu.bme.sch.cmsch.service.RealtimeConfigService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class WarningApiController(
        private val config: RealtimeConfigService
) {

    @JsonView(Preview::class)
    @GetMapping("/warning")
    fun warning(): WarningView {
        return WarningView(
            message = config.getWarningMessage(),
            type = config.getWarningType()
        )
    }

}
