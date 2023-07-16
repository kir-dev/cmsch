package hu.bme.sch.cmsch.config

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.AdminMenuService
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

@Configuration
class SearchableResourceConfig(
    private val adminMenuService: AdminMenuService,
    private val components: List<ComponentBase>
) {

    @PostConstruct
    fun initSearchableResources() {
        components.stream()
            .flatMap { it.getPropertyResources().stream() }
            .forEach { adminMenuService.registerResource(it) }

        components.stream()
            .flatMap { it.getEntityResources().stream() }
            .forEach { adminMenuService.registerResource(it) }
    }

}