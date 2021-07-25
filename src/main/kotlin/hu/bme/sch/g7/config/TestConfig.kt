package hu.bme.sch.g7.config

import hu.bme.sch.g7.dao.NewsRepository
import hu.bme.sch.g7.model.NewsEntity
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class TestConfig(val news: NewsRepository) {

    @PostConstruct
    fun init() {
        news.save(NewsEntity(title = "Az eslő hír", visible = true, highlighted = false))
        news.save(NewsEntity(title = "Az második hír", visible = true, highlighted = true))
        news.save(NewsEntity(title = "Ez nem is hír", visible = true, highlighted = false))
        news.save(NewsEntity(title = "Teszt hír", visible = false, highlighted = false))
    }

}