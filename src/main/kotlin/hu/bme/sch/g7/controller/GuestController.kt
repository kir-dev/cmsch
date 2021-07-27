package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.EventRepository
import hu.bme.sch.g7.dao.NewsRepository
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.GroupEntityDto
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.dto.view.*
import hu.bme.sch.g7.model.UserEntity
import hu.bme.sch.g7.service.LeaderBoardService
import hu.bme.sch.g7.service.RealtimeConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.ZoneId

@RestController
@RequestMapping("/api")
class GuestController(
        val config: RealtimeConfigService,

        val newsRepository: NewsRepository,
        @Value("\${g7web.home.title:}") val title: String,
        @Value("\${g7web.home.interval:}") val interval: String,
        @Value("\${g7web.home.startsAt:0}") val startsAt: Long,

        val eventsRepository: EventRepository,
        @Value("\${g7web.zone-id:CET}") zoneId: String,

        val leaderBoardService: LeaderBoardService
) {

    private val timeZone = ZoneId.of(zoneId)

    @JsonView(Preview::class)
    @GetMapping("/news")
    fun news(): NewsView {
        return NewsView(
                userPreview = supplyUserInformation(),
                title = title,
                interval = interval,
                warningMessage = config.getWarningMessage(),
                startsAt = startsAt,
                news = newsRepository.findTop4ByOrderByTimestamp()
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(): EventsView {
        val events = eventsRepository.findAll()
        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond() * 1000
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond() * 1000
        return EventsView(
                userPreview = supplyUserInformation(),
                warningMessage = config.getWarningMessage(),
                eventsToday = events.filter { it.heldTimestamp > dayStart && it.heldTimestamp < dayEnd },
                allEvents = events
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String): SingleEventView {
        val event = eventsRepository.findByUrl(path)
        return SingleEventView(
                userPreview = supplyUserInformation(),
                event = event.orElse(null)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(): ProfileView {
        val user = fetchUser()

        return ProfileView(
                userPreview = supplyUserInformation(),
                user = user,
                group = user.group?.let { GroupEntityDto(it) }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievements")
    fun achievements(): AchievementsView {
        val user = fetchUser()
        val group = user.group ?: return AchievementsView(
                        userPreview = supplyUserInformation(),
                        groupScore = null,
                        leaderBoard = leaderBoardService.getBoard())

        return AchievementsView(
                userPreview = supplyUserInformation(),
                groupScore = leaderBoardService.getScoreOfGroup(group),
                leaderBoard = leaderBoardService.getBoard(),
                // FIXME: add more fields
        )
    }

    private fun supplyUserInformation(): UserEntityPreview {
        return UserEntityPreview(false)
    }

    private fun fetchUser(): UserEntity {
        return UserEntity()
    }
}
