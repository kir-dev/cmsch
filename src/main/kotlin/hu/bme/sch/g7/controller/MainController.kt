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
        private val config: RealtimeConfigService,
        private val newsRepository: NewsRepository,
        private val eventsRepository: EventRepository,
        @Value("\${g7web.zone-id:CET}") zoneId: String,
        private val leaderBoardService: LeaderBoardService,
        private val achievements: AchievementsService,
        private val extraPagesRepository: ExtraPageRepository,
        private val debtsRepository: SoldProductRepository,
        private val productsRepository: ProductRepository
) {

    private val timeZone = ZoneId.of(zoneId)

    @JsonView(Preview::class)
    @GetMapping("/news")
    fun news(request: HttpServletRequest): NewsView {
        return NewsView(
                warningMessage = config.getWarningMessage(),
                news = newsRepository.findByOrderByTimestamp()
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(request: HttpServletRequest): EventsView {
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond() * 1000
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond() * 1000
        return EventsView(
                warningMessage = config.getWarningMessage(),
                eventsToday = events.filter { it.timestampStart > dayStart && it.timestampStart < dayEnd },
                allEvents = events
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/home")
    fun home(request: HttpServletRequest): HomeView {
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond() * 1000
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond() * 1000
        return HomeView(
                warningMessage = config.getWarningMessage(),
                news = newsRepository.findTop4ByOrderByTimestamp(),
                eventsToday = events.filter { it.timestampStart > dayStart && it.timestampStart < dayEnd },
                achievements = request.getUserOrNull()?.group?.let { achievements.getAllAchievements(it) }
                        ?: achievements.getAllAchievementsForGuests(),
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled()
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String, request: HttpServletRequest): SingleEventView {
        val event = eventsRepository.findByUrl(path)
        return SingleEventView(
                warningMessage = config.getWarningMessage(),
                event = event.orElse(null)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = request.getUser()

        return ProfileView(
                warningMessage = config.getWarningMessage(),
                user = user,
                group = user.group?.let { GroupEntityDto(it) }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/products")
    fun products(request: HttpServletRequest): ProductsView {
        return ProductsView(
                products = productsRepository.findAllByTypeAndVisibleTrue(ProductType.MERCH)
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(request: HttpServletRequest): DebtsView {
        return DebtsView(
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
                warningMessage = config.getWarningMessage(),
                groupScore = null,
                leaderBoard = leaderBoardService.getBoard())

        return AchievementsView(
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
                warningMessage = config.getWarningMessage(),
                achievement = achievement.orElse(null),
                submission = null)

        return SingleAchievementView(
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
                warningMessage = config.getWarningMessage(),
                page = extraPagesRepository.findByUrl(path).orElse(null)
        )
    }

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "v1.0.11"

}
