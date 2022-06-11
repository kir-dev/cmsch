package hu.bme.sch.cmsch.component.extrapage

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageApiController(
    private val extraPagesRepository: ExtraPageRepository,
) {

    @JsonView(FullDetails::class)
    @GetMapping("/page/{path}")
    fun extraPage(@PathVariable path: String, request: HttpServletRequest): ExtraPageView {
        return ExtraPageView(
            page = extraPagesRepository.findByUrlAndVisibleTrue(path).orElse(null)
        )
    }

}
