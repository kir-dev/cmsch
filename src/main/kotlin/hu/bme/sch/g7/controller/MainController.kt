package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.dto.*
import hu.bme.sch.g7.dto.view.*
import hu.bme.sch.g7.model.ProductType
import hu.bme.sch.g7.service.AchievementsService
import hu.bme.sch.g7.service.LeaderBoardService
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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
                warningMessage = config.getWarningMessage(),
                event = event.orElse(null)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = request.getUser()

        return ProfileView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
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
        return DebtsView(
                userPreview = supplyUserInformation(request),
                debts = debtsRepository.findAllByOwnerId(request.getUser().id)
                        .map { DebtDto(
                                it.product,
                                it.price,
                                it.sellerName,
                                it.responsibleName,
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
                warningMessage = config.getWarningMessage(),
                groupScore = null,
                leaderBoard = leaderBoardService.getBoard())

        return AchievementsView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
                groupScore = leaderBoardService.getScoreOfGroup(group),
                leaderBoard = leaderBoardService.getBoard(),
                highlighted = achievements.getHighlightedOnes(group),
                achievements = achievements.getAllAchievements(group)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/{achievementId}")
    fun achievement(@PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val achievement = achievements.getById(achievementId)
        val group = request.getUserOrNull()?.group ?: return SingleAchievementView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
                achievement = achievement.orElse(null),
                submission = null)

        return SingleAchievementView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
                achievement = achievement.orElse(null),
                submission = achievements.getSubmissionOrNull(group, achievement)
        )
    }

    private fun supplyUserInformation(request: HttpServletRequest): UserEntityPreview {
        val user = request.getUserOrNull() ?: return UserEntityPreview(false)
        return UserEntityPreview(true, user.fullName, user.groupName, user.role)
    }

    @PostMapping("/achievement")
    fun submitAchievement(
            @ModelAttribute(binding = false) answer: AchievementSubmissionDto,
            @RequestParam(required = false) file: MultipartFile?,
            request: HttpServletRequest
    ): AchievementSubmissionResponseDto {
        return AchievementSubmissionResponseDto(achievements.submitAchievement(answer, file, request.getUser()))
    }

    @JsonView(FullDetails::class)
    @GetMapping("/extra-page/{path}")
    fun extraPage(@PathVariable path: String, request: HttpServletRequest): ExtraPageView {
        return ExtraPageView(
                userPreview = supplyUserInformation(request),
                warningMessage = config.getWarningMessage(),
                page = extraPagesRepository.findByUrl(path).orElse(null)
        )
    }

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "v1.0.8"

}
