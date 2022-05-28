package hu.bme.sch.cmsch.component.news

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(NewsComponent::class)
class NewsApiController(
    private val newsRepository: NewsRepository
) {

    @JsonView(Preview::class)
    @GetMapping("/news")
    fun news(auth: Authentication): NewsView {
        val user = auth.getUserOrNull()
        return NewsView(
            news = newsRepository.findAllByVisibleTrueOrderByTimestampDesc()
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
        )
    }

}
