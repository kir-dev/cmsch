package hu.bme.sch.g7.controller

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dao.*
import hu.bme.sch.g7.dto.*
import hu.bme.sch.g7.dto.view.*
import hu.bme.sch.g7.g7mobile.LocationResponse
import hu.bme.sch.g7.model.ProductType
import hu.bme.sch.g7.model.RoleType
import hu.bme.sch.g7.model.UserEntity
import hu.bme.sch.g7.service.*
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
//@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "true")
class MainController(
        private val config: RealtimeConfigService,
        private val newsRepository: NewsRepository,
        private val eventsRepository: EventRepository,
        @Value("\${g7web.zone-id:CET}") zoneId: String,
        private val leaderBoardService: LeaderBoardService,
        private val achievements: AchievementsService,
        private val extraPagesRepository: ExtraPageRepository,
        private val debtsRepository: SoldProductRepository,
        private val productsRepository: ProductRepository,
        private val locationService: LocationService,
        private val clock: ClockService,
        private val sessionService: NextJsSessionService
) {

    private val timeZone = ZoneId.of(zoneId)

    @JsonView(Preview::class)
    @GetMapping("/news")
    fun news(request: HttpServletRequest): NewsView {
        val user = request.getUserOrNull()
        return NewsView(
                warningMessage = config.getWarningMessage(),
                news = newsRepository.findAllByVisibleTrueOrderByTimestampDesc()
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
                news = newsRepository.findAllByVisibleTrueOrderByTimestampDesc()
                        .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
                        .take(4),
                upcomingEvents = upcomingEvents,
                achievements = request.getUserOrNull()?.group?.let { achievements.getAllAchievements(it) }
                        ?: achievements.getAllAchievementsForGuests(),
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates()
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
    @GetMapping("/profile/{accessToken}")
    fun profile(@PathVariable accessToken: String, request: HttpServletRequest): ProfileView {
        val user = sessionService.getUser(accessToken) ?: return ProfileView(false, UNKNOWN_USER, group = null)
        if (config.isSiteLowProfile())
            return ProfileView(false, UNKNOWN_USER, group = null)

        val group = user.group?.let { GroupEntityDto(it) }
        return ProfileView(
                loggedin = true,
                user = user,
                group = group,
                locations = locationService.findLocationsOfGroup(group?.name ?: "")
                        .filter { it.timestamp + 600 > clock.getTimeInSeconds() }
                        .map { GroupMemberLocationDto(
                                if (it.alias.isNotBlank()) it.alias else it.userName,
                                it.longitude,
                                it.latitude,
                                it.accuracy,
                                it.timestamp
                        ) },
                debts = debtsRepository.findAllByOwnerId(user.id)
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

    @JsonView(FullDetails::class)
//    @GetMapping("/products")
    fun products(request: HttpServletRequest): ProductsView {
        if (config.isSiteLowProfile())
            return ProductsView(products = listOf())

        return ProductsView(products = productsRepository.findAllByTypeAndVisibleTrue(ProductType.MERCH))
    }

    @JsonView(FullDetails::class)
//    @GetMapping("/debts")
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
    @GetMapping("/achievement/{accessToken}")
    fun achievements(@PathVariable accessToken: String, request: HttpServletRequest): AchievementsView {
        if (config.isSiteLowProfile()) {
            return AchievementsView(
                    groupScore = null,
                    leaderBoard = listOf(),
                    leaderBoardVisible = config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates())
        }

        val group = sessionService.getUser(accessToken)?.group ?: return AchievementsView(
                groupScore = null,
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates())

        return AchievementsView(
                groupScore = leaderBoardService.getScoreOfGroup(group),
                categories = achievements.getCategories(group.id)
                        .filter { it.availableFrom < clock.getTimeInSeconds() },
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates()
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/{accessToken}/category/{categoryId}")
    fun achievementCategory(@PathVariable accessToken: String, @PathVariable categoryId: Int, request: HttpServletRequest): AchievementCategoryView {
        if (config.isSiteLowProfile() || achievements.getCategoryAvailableFrom(categoryId) > clock.getTimeInSeconds()) {
            return AchievementCategoryView(
                categoryName = "Még nem publikus O.o",
                achievements = listOf()
            )
        }

        val group = sessionService.getUser(accessToken)?.group ?: return AchievementCategoryView(
                categoryName = "Nem található",
                achievements = listOf()
        )

        return AchievementCategoryView(
                categoryName = achievements.getCategoryName(categoryId),
                achievements = achievements.getAllAchievements(group).filter { it.achievement.categoryId == categoryId }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/{accessToken}/submit/{achievementId}")
    fun achievement(@PathVariable accessToken: String, @PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val achievement = achievements.getById(achievementId)
        if (achievement.orElse(null)?.visible?.not() ?: false || config.isSiteLowProfile())
            return SingleAchievementView(warningMessage = config.getWarningMessage(), achievement = null, submission = null)

        val group = sessionService.getUser(accessToken)?.group ?: return SingleAchievementView(
                warningMessage = config.getWarningMessage(),
                achievement = achievement.orElse(null),
                submission = null,
                status = AchievementStatus.NOT_SUBMITTED
        )

        val submission = achievements.getSubmissionOrNull(group, achievement)
        return SingleAchievementView(
                warningMessage = config.getWarningMessage(),
                achievement = achievement.orElse(null),
                submission = submission,
                status = if (submission?.approved ?: false) AchievementStatus.ACCEPTED
                    else if (submission?.rejected ?: false) AchievementStatus.REJECTED
                    else if (!(submission?.approved ?: true) && !(submission?.rejected ?: true)) AchievementStatus.SUBMITTED
                    else AchievementStatus.NOT_SUBMITTED
        )
    }

    @ResponseBody
    @PostMapping("/achievement/{accessToken}/submit")
    fun submitAchievement(
            @ModelAttribute(binding = false) answer: AchievementSubmissionDto,
            @RequestParam(required = false) file: MultipartFile?,
            @PathVariable accessToken: String,
            request: HttpServletRequest
    ): AchievementSubmissionResponseDto {
        if (config.isSiteLowProfile())
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        val user = sessionService.getUser(accessToken) ?:
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)
        return AchievementSubmissionResponseDto(achievements.submitAchievement(answer, file, user))
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
    @PostMapping("/location")
    fun pushLocation(@RequestBody payload: LocationDto): LocationResponse {
        return locationService.pushLocation(payload)
    }

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "v1.0.22"

}
