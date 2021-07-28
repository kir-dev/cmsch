package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.dto.DebtDto
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.GroupEntityDto
import hu.bme.sch.g7.dto.Preview
import hu.bme.sch.g7.dto.view.*
import hu.bme.sch.g7.model.ProductType
import hu.bme.sch.g7.model.UserEntity
import hu.bme.sch.g7.service.AchievementsService
import hu.bme.sch.g7.service.LeaderBoardService
import hu.bme.sch.g7.service.RealtimeConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.ZoneId

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "true")
class MainController(
        val config: RealtimeConfigService,

        val newsRepository: NewsRepository,
        @Value("\${g7web.home.title:}") val title: String,
        @Value("\${g7web.home.interval:}") val interval: String,
        @Value("\${g7web.home.startsAt:0}") val startsAt: Long,

        val eventsRepository: EventRepository,
        @Value("\${g7web.zone-id:CET}") zoneId: String,

        val leaderBoardService: LeaderBoardService,
        val achievements: AchievementsService,
        val extraPagesRepository: ExtraPageRepository,
        val debtsRepository: SoldProductRepository,
        val productsRepository: ProductRepository
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
    @GetMapping("/products")
    fun products(): ProductsView {
        return ProductsView(
                userPreview = supplyUserInformation(),
                products = productsRepository.findAllByTypeAndVisibleTrue(ProductType.MERCH)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(): DebtsView {
        val user = fetchUser()

        return DebtsView(
                userPreview = supplyUserInformation(),
                debts = debtsRepository.findAllByOwner_Id(user.id)
                        .map { DebtDto(
                                it.product?.name ?: "n/a",
                                it.seller?.fullName ?: "n/a",
                                "TODO Representative", // FIXME: This feature is not implemented
                                it.payed,
                                it.shipped,
                                it.log
                        ) }
        )
    }

    @JsonView(Preview::class)
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
                highlighted = achievements.getHighlightedOnes(group),
                achievements = achievements.getAllAchievements(group)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/{achievementId}")
    fun achievement(@PathVariable achievementId: Int): SingleAchievementView {
        val user = fetchUser()
        val achievement = achievements.getById(achievementId)
        val group = user.group ?: return SingleAchievementView(
                userPreview = supplyUserInformation(),
                achievement = achievement.orElse(null),
                submission = null)

        return SingleAchievementView(
                userPreview = supplyUserInformation(),
                achievement = achievement.orElse(null),
                submission = achievements.getSubmissionOrNull(group, achievement)
        )
    }

    private fun supplyUserInformation(): UserEntityPreview {
        return UserEntityPreview(false)
    }

    private fun fetchUser(): UserEntity {
        return UserEntity()
    }

    @JsonView(FullDetails::class)
    @GetMapping("/extra-page/{path}")
    fun extraPage(@PathVariable path: String): ExtraPageView {
        return ExtraPageView(
                userPreview = supplyUserInformation(),
                page = extraPagesRepository.findByUrl(path).orElse(null)
        )
    }

}
