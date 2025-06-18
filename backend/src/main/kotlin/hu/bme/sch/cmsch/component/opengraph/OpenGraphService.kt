package hu.bme.sch.cmsch.component.opengraph

import hu.bme.sch.cmsch.component.event.EventRepository
import hu.bme.sch.cmsch.component.news.NewsRepository
import hu.bme.sch.cmsch.component.staticpage.StaticPageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OpenGraphService(
    private val staticPageRepository: Optional<StaticPageRepository>,
    private val eventRepository: Optional<EventRepository>,
    private val newsRepository: Optional<NewsRepository>,
) {

    @Transactional(readOnly = true)
    fun findExtraPage(url: String): Optional<OpenGraphResource> {
        return staticPageRepository.flatMap { it.findByUrl(url) }
    }

    @Transactional(readOnly = true)
    fun findEvent(url: String): Optional<OpenGraphResource> {
        return eventRepository.flatMap { it.findByUrl(url) }
    }

    @Transactional(readOnly = true)
    fun findNews(url: String): Optional<OpenGraphResource> {
        return newsRepository.flatMap { it.findByUrl(url) }
    }

}
