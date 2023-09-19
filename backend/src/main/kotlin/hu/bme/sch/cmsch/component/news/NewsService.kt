package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(NewsComponent::class)
open class NewsService(
    private val newsRepository: NewsRepository,
    private val clock: TimeService
) {

    @Transactional(readOnly = true)
    open fun fetchNews(user: CmschUser?): List<NewsEntity> {
        val now = clock.getTimeInSeconds()
        return newsRepository.findAllByVisibleTrueOrderByTimestampDesc()
            .filter { clock.isTimePassed(it.timestamp, now) }
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
    }

    @Transactional(readOnly = true)
    open fun fetchSpecificNews(user: CmschUser?, path: String): Optional<ResponseEntity<NewsEntity>> {
        val now = clock.getTimeInSeconds()
        return newsRepository.findByUrlAndVisibleTrue(path)
            .filter { clock.isTimePassed(it.timestamp, now) }
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
            .map { ResponseEntity.ok(it) }
    }

}
