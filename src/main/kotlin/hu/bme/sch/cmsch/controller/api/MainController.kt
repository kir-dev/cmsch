package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.achievement.AchievementsService
import hu.bme.sch.cmsch.component.debt.*
import hu.bme.sch.cmsch.component.event.EventRepository
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.component.location.LocationService
import hu.bme.sch.cmsch.component.news.NewsRepository
import hu.bme.sch.cmsch.component.debt.DebtDto
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.component.location.LocationDto
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.HomeView
import hu.bme.sch.cmsch.component.location.LocationResponse
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.RealtimeConfigService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import javax.servlet.http.HttpServletRequest

val UNKNOWN_USER = UserEntity(0, fullName = "Feature Not Available")

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class MainController(
    private val config: RealtimeConfigService,
    private val newsRepository: NewsRepository,
    private val eventsRepository: EventRepository,
    @Value("\${cmsch.zone-id:CET}") zoneId: String,
    private val leaderBoardService: LeaderBoardService,
    private val achievements: AchievementsService,
    private val debtsRepository: SoldProductRepository,
    private val productsRepository: ProductRepository,
    private val locationService: LocationService,
    private val clock: ClockService
) {
    private val timeZone = ZoneId.of(zoneId)

    private val formatter = SimpleDateFormat("yyyy.MM.dd. HH:mm:ss")

    @ResponseBody
    @GetMapping("/version")
    fun version(): String = "2.6.5"

    @ResponseBody
    @GetMapping("/time")
    fun time(): String = formatter.format(clock.getTimeInSeconds())

    @JsonView(Preview::class)
//    @GetMapping("/home")
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
                news = newsRepository.findAllByVisibleTrueOrderByTimestampDesc()
                        .filter { (user?.role ?: RoleType.GUEST).value >= it.minRole.value }
                        .take(4),
                upcomingEvents = upcomingEvents,
                achievements = request.getUserOrNull()?.group?.let { achievements.getAllAchievementsForGroup(it) }
                        ?: achievements.getAllAchievementsForGuests(),
                leaderBoard = leaderBoardService.getBoardForGroups(),
                leaderBoardVisible = config.isLeaderBoardEnabled(),
                leaderBoardFrozen = !config.isLeaderBoardUpdates()
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

    @ResponseBody
//    @PostMapping("/location")
    fun pushLocation(@RequestBody payload: LocationDto): LocationResponse {
        return locationService.pushLocation(payload)
    }

}
