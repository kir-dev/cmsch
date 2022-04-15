package hu.bme.sch.cmsch.component.app

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ApplicationComponent::class)
class ApplicationApiController {

    @GetMapping("/app")
    fun app(): ApplicationConfigDto {
        return ApplicationConfigDto(
            listOf(),
            mapOf())
    }

}
