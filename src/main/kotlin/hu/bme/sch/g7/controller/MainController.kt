package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.dto.*
import hu.bme.sch.g7.dto.view.*
import hu.bme.sch.g7.model.ProductType
import hu.bme.sch.g7.model.RoleType
import hu.bme.sch.g7.model.UserEntity
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

val UNKNOWN_USER = UserEntity(0, fullName = "Feature Not Available")

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
        val user = request.getUserOrNull()
        return NewsView(
                warningMessage = config.getWarningMessage(),
                news = newsRepository.findAllByVisibleTrueOrderByTimestamp()
                        .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/events")
    fun events(request: HttpServletRequest): EventsView {
        val user = request.getUserOrNull()
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        return EventsView(
                warningMessage = config.getWarningMessage(),
                allEvents = events
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/home")
    fun home(request: HttpServletRequest): HomeView {
        val user = request.getUserOrNull()
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond()
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond()
        var upcomingEvents = events.filter { it.timestampStart > dayStart && it.timestampStart < dayEnd }
        if (upcomingEvents.isEmpty())
            upcomingEvents = events.filter { it.timestampStart >= dayStart }.take(6)

        return HomeView(
                warningMessage = config.getWarningMessage(),
                news = newsRepository.findAllByVisibleTrueOrderByTimestamp()
                        .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
                        .take(4),
                upcomingEvents = upcomingEvents,
                achievements = request.getUserOrNull()?.group?.let { achievements.getAllAchievements(it) }
                        ?: achievements.getAllAchievementsForGuests(),
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled()
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/events/{path}")
    fun event(@PathVariable path: String, request: HttpServletRequest): SingleEventView {
        val event = eventsRepository.findByUrl(path).orElse(null)

        return SingleEventView(
                warningMessage = config.getWarningMessage(),
                event = event
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/profile")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = request.getUser()
        if (config.isSiteLowProfile())
            return ProfileView(warningMessage = config.getWarningMessage(), UNKNOWN_USER, group = null)

        return ProfileView(
                warningMessage = config.getWarningMessage(),
                user = user,
                group = user.group?.let { GroupEntityDto(it) }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/products")
    fun products(request: HttpServletRequest): ProductsView {
        if (config.isSiteLowProfile())
            return ProductsView(products = listOf())

        return ProductsView(products = productsRepository.findAllByTypeAndVisibleTrue(ProductType.MERCH))
    }

    @JsonView(FullDetails::class)
    @GetMapping("/debts")
    fun debts(request: HttpServletRequest): DebtsView {
        if (config.isSiteLowProfile())
            return DebtsView(debts = listOf())

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
        if (config.isSiteLowProfile()) {
            return AchievementsView(
                    warningMessage = config.getWarningMessage(),
                    groupScore = null,
                    leaderBoard = listOf())
        }

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
        if (achievement.orElse(null)?.visible ?: false || config.isSiteLowProfile())
            return SingleAchievementView(warningMessage = config.getWarningMessage(), achievement = null, submission = null)

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

    @PostMapping("/achievement")
    fun submitAchievement(
            @ModelAttribute(binding = false) answer: AchievementSubmissionDto,
            @RequestParam(required = false) file: MultipartFile?,
            request: HttpServletRequest
    ): AchievementSubmissionResponseDto {
        if (config.isSiteLowProfile())
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        return AchievementSubmissionResponseDto(achievements.submitAchievement(answer, file, request.getUser()))
    }

    @JsonView(FullDetails::class)
    @GetMapping("/extra-page/{path}")
    fun extraPage(@PathVariable path: String, request: HttpServletRequest): ExtraPageView {
        if (config.isSiteLowProfile())
            return ExtraPageView(warningMessage = config.getWarningMessage(), page = null)

        return ExtraPageView(
                warningMessage = config.getWarningMessage(),
                page = extraPagesRepository.findByUrlAndVisibleTrue(path).orElse(null)
        )
    }

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "v1.0.17"

}
