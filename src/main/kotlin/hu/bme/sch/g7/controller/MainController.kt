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
@CrossOrigin(origins = ["\${g7web.frontend.production-url}"], allowedHeaders = ["*"])
class MainController(
        private val config: RealtimeConfigService,
        private val newsRepository: NewsRepository,
        private val eventsRepository: EventRepository,
        @Value("\${cmsch.zone-id:CET}") zoneId: String,
        private val leaderBoardService: LeaderBoardService,
        private val achievements: AchievementsService,
        private val extraPagesRepository: ExtraPageRepository,
        private val debtsRepository: SoldProductRepository,
        private val productsRepository: ProductRepository,
        private val locationService: LocationService,
        private val clock: ClockService
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
    @GetMapping("/home")
    fun home(request: HttpServletRequest): HomeView {
        val user = request.getUserOrNull()
        val events = eventsRepository.findAllByVisibleTrueOrderByTimestampStart()
                .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }

        val dayStart = LocalDate.now(timeZone).atStartOfDay(timeZone).toEpochSecond()
        val dayEnd = LocalDate.now(timeZone).plusDays(1).atStartOfDay(timeZone).toEpochSecond()
        var upcomingEvents = events.filter { it.timestampStart in (dayStart + 1) until dayEnd }
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
    @GetMapping("/profile/")
    fun profile(request: HttpServletRequest): ProfileView {
        val user = request.getUserOrNull() ?: return ProfileView(false, UNKNOWN_USER, group = null)
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
                                it.alias.ifBlank { it.userName },
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
                                it.log,
                                it.materialIcon
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
                                it.log,
                                it.materialIcon
                        ) }
        )
    }

    @JsonView(Preview::class)
    @GetMapping("/achievement")
    fun achievements(request: HttpServletRequest): AchievementsView {
        if (config.isSiteLowProfile()) {
            return AchievementsView(
                    groupScore = null,
                    leaderBoard = listOf(),
                    leaderBoardVisible = config.isLeaderBoardEnabled(),
                    leaderBoardFrozen = !config.isLeaderBoardUpdates())
        }

        val group = request.getUserOrNull()?.group ?: return AchievementsView(
                groupScore = null,
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates())

        return AchievementsView(
                groupScore = leaderBoardService.getScoreOfGroup(group),
                categories = achievements.getCategories(group.id)
                        .filter { it.availableFrom < clock.getTimeInSeconds() && it.availableTo > clock.getTimeInSeconds() },
                leaderBoard = leaderBoardService.getBoard(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates()
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/category/{categoryId}")
    fun achievementCategory(@PathVariable categoryId: Int, request: HttpServletRequest): AchievementCategoryView {
        val category = achievements.getCategory(categoryId) ?: return AchievementCategoryView(
                categoryName = "Nem található O.o",
                achievements = listOf()
        )

        if (config.isSiteLowProfile() || category.availableFrom > clock.getTimeInSeconds() || category.availableTo < clock.getTimeInSeconds()) {
            return AchievementCategoryView(
                categoryName = "Még nem publikus O.o",
                achievements = listOf()
            )
        }

        val group = request.getUserOrNull()?.group ?: return AchievementCategoryView(
                categoryName = "Nem található",
                achievements = listOf()
        )

        return AchievementCategoryView(
                categoryName = category.name,
                achievements = achievements.getAllAchievements(group).filter { it.achievement.categoryId == categoryId }
        )
    }

    @JsonView(FullDetails::class)
    @GetMapping("/achievement/submit/{achievementId}")
    fun achievement(@PathVariable achievementId: Int, request: HttpServletRequest): SingleAchievementView {
        val achievement = achievements.getById(achievementId)
        if (achievement.orElse(null)?.visible?.not() == true || config.isSiteLowProfile())
            return SingleAchievementView(warningMessage = config.getWarningMessage(), achievement = null, submission = null)

        val group = request.getUserOrNull()?.group ?: return SingleAchievementView(
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
                status = if (submission?.approved == true) AchievementStatus.ACCEPTED
                    else if (submission?.rejected == true) AchievementStatus.REJECTED
                    else if (submission?.approved == false && !submission.rejected) AchievementStatus.SUBMITTED
                    else AchievementStatus.NOT_SUBMITTED
        )
    }

    @ResponseBody
    @PostMapping("/achievement/submit")
    fun submitAchievement(
            @ModelAttribute(binding = false) answer: AchievementSubmissionDto,
            @RequestParam(required = false) file: MultipartFile?,
            request: HttpServletRequest
    ): AchievementSubmissionResponseDto {
        if (config.isSiteLowProfile())
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)

        val user = request.getUserOrNull() ?:
            return AchievementSubmissionResponseDto(AchievementSubmissionStatus.NO_PERMISSION)
        return AchievementSubmissionResponseDto(achievements.submitAchievement(answer, file, user))
    }

    @JsonView(FullDetails::class)
//    @GetMapping("/extra-page/{path}")
    fun extraPage(@PathVariable path: String, request: HttpServletRequest): ExtraPageView {
        if (config.isSiteLowProfile())
            return ExtraPageView(warningMessage = config.getWarningMessage(), page = null)

        return ExtraPageView(
                warningMessage = config.getWarningMessage(),
                page = extraPagesRepository.findByUrlAndVisibleTrue(path).orElse(null)
        )
    }

    @ResponseBody
//    @PostMapping("/location")
    fun pushLocation(@RequestBody payload: LocationDto): LocationResponse {
        return locationService.pushLocation(payload)
    }

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "v1.1.27"

}
