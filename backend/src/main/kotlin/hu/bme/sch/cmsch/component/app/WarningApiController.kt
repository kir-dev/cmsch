package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ApplicationComponent::class)
class WarningApiController(
    private val applicationComponent: ApplicationComponent
) {

    @JsonView(Preview::class)
    @GetMapping("/warning")
    fun warning(): WarningView {
        return WarningView(
            message = applicationComponent.warningMessage.getValue(),
            type = applicationComponent.warningLevel.getValue()
        )
    }

}
