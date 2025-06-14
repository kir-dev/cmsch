package hu.bme.sch.cmsch.component.home

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.component.news.NewsService
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(HomeComponent::class)
class HomeApiController(
    private val newsService: Optional<NewsService>,
    private val homeComponent: HomeComponent,
) {

    @JsonView(Preview::class)
    @GetMapping("/home/news")
    fun home(auth: Authentication?): List<NewsEntity> {
        val user = auth.getUserOrNull()
        if (!homeComponent.showNews.getValue())
            return listOf()
        return newsService.map { it.fetchNews(user) }
            .orElse(listOf())
            .take(homeComponent.maxVisibleCount.getValue().toInt())
    }

}
