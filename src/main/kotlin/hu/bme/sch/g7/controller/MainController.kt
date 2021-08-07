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
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.ZoneId
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
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
    fun news(request: HttpServletRequest): NewsView {
        return NewsView(
                userPreview = supplyUserInformation(request),
                title = title,
                interval = interval,
                warningMessage = config.getWarningMessage(),
                startsAt = startsAt,
                news = newsRepository.findTop4ByOrderByTimestamp()
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(request: HttpServletRequest): EventsView {
        val events = eventsRepository.findAll()
        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond() * 1000
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond() * 1000
        return EventsView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
                eventsToday = events.filter { it.heldTimestamp > dayStart && it.heldTimestamp < dayEnd },
                allEvents = events
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String, request: HttpServletRequest): SingleEventView {
        val event = eventsRepository.findByUrl(path)
        return SingleEventView(
                userPreview = supplyUserInformation(request),
                event = event.orElse(null)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = fetchUser()

        return ProfileView(
                userPreview = supplyUserInformation(request),
                user = user,
                group = user.group?.let { GroupEntityDto(it) }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/products")
    fun products(request: HttpServletRequest): ProductsView {
        return ProductsView(
                userPreview = supplyUserInformation(request),
                products = productsRepository.findAllByTypeAndVisibleTrue(ProductType.MERCH)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(request: HttpServletRequest): DebtsView {
        val user = fetchUser()

        return DebtsView(
                userPreview = supplyUserInformation(request),
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
    fun achievements(request: HttpServletRequest): AchievementsView {
        val group = request.getUserOrNull()?.group ?: return AchievementsView(
                userPreview = supplyUserInformation(request),
                groupScore = null,
                leaderBoard = leaderBoardService.getBoard())

        return AchievementsView(
                userPreview = supplyUserInformation(request),
                groupScore = leaderBoardService.getScoreOfGroup(group),
                leaderBoard = leaderBoardService.getBoard(),
                highlighted = achievements.getHighlightedOnes(group),
                achievements = achievements.getAllAchievements(group)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/{achievementId}")
    fun achievement(@PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val user = fetchUser()
        val achievement = achievements.getById(achievementId)
        val group = user.group ?: return SingleAchievementView(
                userPreview = supplyUserInformation(request),
                achievement = achievement.orElse(null),
                submission = null)

        return SingleAchievementView(
                userPreview = supplyUserInformation(request),
                achievement = achievement.orElse(null),
                submission = achievements.getSubmissionOrNull(group, achievement)
        )
    }

    private fun supplyUserInformation(request: HttpServletRequest): UserEntityPreview {
        val user = request.getUserOrNull() ?: return UserEntityPreview(false)
        return UserEntityPreview(true, user.fullName, user.groupName, user.role)
    }

    private fun fetchUser(): UserEntity {
        return UserEntity()
    }

    @JsonView(FullDetails::class)
    @GetMapping("/extra-page/{path}")
    fun extraPage(@PathVariable path: String, request: HttpServletRequest): ExtraPageView {
        return ExtraPageView(
                userPreview = supplyUserInformation(request),
                page = extraPagesRepository.findByUrl(path).orElse(null)
        )
    }

}
