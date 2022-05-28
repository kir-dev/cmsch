package hu.bme.sch.cmsch.component.home

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.achievement.AchievementsService
import hu.bme.sch.cmsch.component.event.EventRepository
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.component.news.NewsRepository
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ClockService
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
    private val clock: ClockService,
    private val leaderBoardService: Optional<LeaderBoardService>,
    private val newsRepository: Optional<NewsRepository>,
    private val eventsRepository: Optional<EventRepository>,
    private val achievements: Optional<AchievementsService>
) {

    @JsonView(Preview::class)
    @GetMapping("/home")
    fun home(auth: Authentication): HomeView {
        val user = auth.getUserFromDatabaseOrNull()
        val events = eventsRepository.map { it.findAllByVisibleTrueOrderByTimestampStart() }
            .orElse(listOf())
            .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        val dayStart = LocalDate.now(clock.timeZone).atStartOfDay(clock.timeZone).toEpochSecond()
        val dayEnd = LocalDate.now(clock.timeZone).plusDays(1).atStartOfDay(clock.timeZone).toEpochSecond()
        var upcomingEvents = events.filter { it.timestampStart in (dayStart + 1) until dayEnd }
        if (upcomingEvents.isEmpty())
            upcomingEvents = events.filter { it.timestampStart >= dayStart }.take(6)

        return HomeView(
            news = newsRepository.map { it.findAllByVisibleTrueOrderByTimestampDesc() }
                .orElse(listOf())
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
                .take(4),
            upcomingEvents = upcomingEvents,
            achievements = achievements.map { achievementsService ->
                user?.group?.let { achievementsService.getAllAchievementsForGroup(it) }
                    ?: achievementsService.getAllAchievementsForGuests()
            }.orElse(listOf()),
            leaderBoard = leaderBoardService.map { it.getBoardForGroups() }.orElse(listOf()),
        )
    }


}
