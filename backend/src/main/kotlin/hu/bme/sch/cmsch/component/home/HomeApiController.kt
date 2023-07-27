package hu.bme.sch.cmsch.component.home

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.task.TasksService
import hu.bme.sch.cmsch.component.event.EventRepository
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.component.news.NewsComponent
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.component.news.NewsRepository
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(HomeComponent::class)
class HomeApiController(
    private val clock: TimeService,
    private val leaderBoardService: Optional<LeaderBoardService>,
    private val newsRepository: Optional<NewsRepository>,
    private val newsComponent: Optional<NewsComponent>,
    private val homeComponent: HomeComponent,
    private val eventsRepository: Optional<EventRepository>,
    private val tasks: Optional<TasksService>
) {

    @JsonView(Preview::class)
    @GetMapping("/home/news")
    fun home(auth: Authentication?): List<NewsEntity> {
        val user = auth.getUserOrNull()
        if (!homeComponent.showNews.isValueTrue())
            return listOf()
        return newsRepository.map { it.findAllByVisibleTrueOrderByTimestampDesc() }
            .orElse(listOf())
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
            .take(homeComponent.maxVisibleCount.getValue().toIntOrNull() ?: 0)
    }

    @JsonView(Preview::class)
    fun legacyHome(auth: Authentication): LegacyHomeView {
        val user = auth.getUserFromDatabaseOrNull()
        val events = eventsRepository.map { it.findAllByVisibleTrueOrderByTimestampStart() }
            .orElse(listOf())
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        val dayStart = LocalDate.now(clock.timeZone).atStartOfDay(clock.timeZone).toEpochSecond()
        val dayEnd = LocalDate.now(clock.timeZone).plusDays(1).atStartOfDay(clock.timeZone).toEpochSecond()
        var upcomingEvents = events.filter { it.timestampStart in (dayStart + 1) until dayEnd }
        if (upcomingEvents.isEmpty())
            upcomingEvents = events.filter { it.timestampStart >= dayStart }.take(6)

        return LegacyHomeView(
            news = newsRepository.map { it.findAllByVisibleTrueOrderByTimestampDesc() }
                .orElse(listOf())
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
                .take(homeComponent.maxVisibleCount.getValue().toIntOrNull() ?: 0),
            upcomingEvents = upcomingEvents,
            tasks = tasks.map { tasksService ->
                user?.group?.let { tasksService.getAllTasksForGroup(it) }
                    ?: tasksService.getAllTasksForGuests()
            }.orElse(listOf()),
            leaderBoard = leaderBoardService.map { it.getBoardForGroups() }.orElse(listOf()),
        )
    }

}
