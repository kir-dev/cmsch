package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.achievement.SubmittedAchievementRepository
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.debt.ProductService
import hu.bme.sch.cmsch.dto.TopListAsGroupEntryDto
import hu.bme.sch.cmsch.dto.TopListAsUserEntryDto
import hu.bme.sch.cmsch.dto.virtual.CheckRatingVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.GroupMemberVirtualEntity
import hu.bme.sch.cmsch.component.location.TrackGroupVirtualEntity
import hu.bme.sch.cmsch.component.location.LocationService
import hu.bme.sch.cmsch.component.debt.SoldProductEntity
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardComponent
import hu.bme.sch.cmsch.component.leaderboard.LeaderBoardService
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_TOPLIST = "toplist"
const val CONTROL_MODE_PAYED = "payed"
const val CONTROL_MODE_NONE = "none"
const val CONTROL_MODE_TRACK = "track"
const val CONTROL_MODE_PDF = "pdf"

@Controller
@RequestMapping("/admin/control")
class AdminPanelCustomController(
    private val leaderBoardService: LeaderBoardService,
    private val productService: ProductService,
    private val userService: UserService,
    private val leaderBoardComponent: LeaderBoardComponent,
    private val applicationComponent: ApplicationComponent,
    private val adminMenuService: AdminMenuService,
    private val submittedRepository: SubmittedAchievementRepository,
    private val locationService: LocationService,
    @Value("\${cmsch.profile.qr-prefix:KIRDEV_}") private val prefix: String
) {

    private val groupTopListDescriptor = OverviewBuilder(TopListAsGroupEntryDto::class)
    private val userTopListDescriptor = OverviewBuilder(TopListAsUserEntryDto::class)
    private val debtsDescriptor = OverviewBuilder(SoldProductEntity::class)
    private val membersDescriptor = OverviewBuilder(GroupMemberVirtualEntity::class)
    private val submittedDescriptor = OverviewBuilder(CheckRatingVirtualEntity::class)
    private val trackDescriptor = OverviewBuilder(TrackGroupVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuGroup("Általános", 0))
        adminMenuService.registerEntry(javaClass.simpleName, AdminMenuEntry("Kezdő menü", "home",
                "/admin/control/basics", 1, PERMISSION_AT_LEAST_STAFF))
        adminMenuService.registerEntry(javaClass.simpleName, AdminMenuEntry("Oldal megyitása", "launch",
                "/control/open-site", 2, PERMISSION_AT_LEAST_STAFF))

    }

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    @GetMapping("/basics")
    fun dashboard(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("user", user)
        model.addAttribute("motd", applicationComponent.motd.getValue())
        model.addAttribute("website", applicationComponent.siteUrl.getValue())
        model.addAttribute("staffMessage", applicationComponent.staffMessage.getValue())

        return "admin"
    }

    @GetMapping("/group-toplist")
    fun groupToplist(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Tankör Toplista")
        model.addAttribute("description", "Tankörök helyezése a pontversenyben. " +
                "A pontok 10 percenként számítódnak újra. " +
                "Manuális újrageneráláshoz van egy gomb a lap alján.")
        model.addAttribute("view", "group-toplist")
        model.addAttribute("columns", groupTopListDescriptor.getColumns())
        model.addAttribute("fields", groupTopListDescriptor.getColumnDefinitions())
        model.addAttribute("rows", leaderBoardService.getBoardAnywaysForGroups())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_TOPLIST)
        model.addAttribute("leaderboardEnabled", leaderBoardComponent.leaderboardEnabled.isValueTrue())
        model.addAttribute("leaderboardUpdates", !leaderBoardComponent.leaderboardFrozen.isValueTrue())

        return "overview"
    }

    @GetMapping("/group-toplist/refresh")
    fun refreshGroupTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardService.forceRecalculateForGroups()
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-enable")
    fun enableRefreshGroupTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("false")
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-disable")
    fun disableRefreshGroupTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("true")
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/user-toplist")
    fun userToplist(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Felhasználó Toplista")
        model.addAttribute("description", "Felhasználó helyezése a pontversenyben. " +
                "A pontok 10 percenként számítódnak újra. " +
                "Manuális újrageneráláshoz van egy gomb a lap alján.")
        model.addAttribute("view", "user-toplist")
        model.addAttribute("columns", userTopListDescriptor.getColumns())
        model.addAttribute("fields", userTopListDescriptor.getColumnDefinitions())
        model.addAttribute("rows", leaderBoardService.getBoardAnywaysForUsers())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_TOPLIST)
        model.addAttribute("leaderboardEnabled", leaderBoardComponent.leaderboardEnabled.isValueTrue())
        model.addAttribute("leaderboardUpdates", !leaderBoardComponent.leaderboardFrozen.isValueTrue())

        return "overview"
    }

    @GetMapping("/user-toplist/refresh")
    fun refreshUserTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardService.forceRecalculateForUsers()
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-enable")
    fun enableRefreshUserTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("true")
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-disable")
    fun disableRefreshUserTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantMedia }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("false")
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/check-ratings")
    fun viewAll(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantRateAchievement }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Pontok ellenőrzése")
        model.addAttribute("description", "Itt azok a beadások láthatóak amik eltérnek a beadásra adható max ponttól vagy a 0 ponttól.")
        model.addAttribute("view", "rate-achievements")
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchSubmittedChecks())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    private fun fetchSubmittedChecks(): List<CheckRatingVirtualEntity> {
        return submittedRepository.findAllByScoreGreaterThanAndApprovedIsTrue(0)
                .filter { it.score != (it.achievement?.maxScore ?: 0) }
                .map { CheckRatingVirtualEntity(it.id, it.groupName, it.score, it.achievement?.maxScore ?: 0) }
    }

    @GetMapping("/debts-of-my-group")
    fun debtsOfMyGroup(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger || it.grantFinance }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Tanköröm tartozásai")
        model.addAttribute("titleSingular", "Tartozás")
        model.addAttribute("description", "Ha a tartozáshoz a pénzt odaadta neked a kolléga, akkor pipáld ki itt. " +
                "Onnantól a te felelősséged lesz majd elszámolni a gazdaságisnak.")
        model.addAttribute("view", "debts-of-my-group")
        model.addAttribute("columns", debtsDescriptor.getColumns())
        model.addAttribute("fields", debtsDescriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllDebtsByGroup(request.getUser()))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_PAYED)

        return "overview"
    }

    @GetMapping("/my-debts")
    fun myDebts(model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", "Saját tartozásaim")
        model.addAttribute("titleSingular", "Saját tartozásaim")
        model.addAttribute("description", "Ezekkel a tételekkel a reszortgdaságisnak kell elszámolnod. A pontos módról emailben értesülhetsz.")
        model.addAttribute("view", "my-debts")
        model.addAttribute("columns", debtsDescriptor.getColumns())
        model.addAttribute("fields", debtsDescriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllDebtsByUser(request.getUser()))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    @GetMapping("/debts-of-my-group/payed/{id}")
    fun setDebtsStatus(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger || it.grantFinance }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Tartozás")
        model.addAttribute("view", "debts-of-my-group")
        model.addAttribute("id", id)
        model.addAttribute("user", request.getUser())

        val entity = productService.findTransactionById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("item", entity.orElseThrow().toString())
        }
        return "payed"
    }

    @PostMapping("/debts-of-my-group/payed/{id}")
    fun payed(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger || it.grantFinance }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        // TODO: Check group here

        productService.setTransactionPayed(id, request.getUser())
        return "redirect:/admin/control/debts-of-my-group"
    }

    @GetMapping("/members-of-my-group")
    fun membersOfMyGroup(model: Model, request: HttpServletRequest): String {

        model.addAttribute("title", "Tanköröm tagjai")
        model.addAttribute("description", "A tankörödben lévő emberek. Ameddig valaki nem jelentkezik be, addig itt nem látszik, hogy rendező-e.")
        model.addAttribute("view", "members-of-my-group")
        model.addAttribute("columns", membersDescriptor.getColumns())
        model.addAttribute("fields", membersDescriptor.getColumnDefinitions())
        model.addAttribute("rows", userService.allMembersOfGroup(request.getUser().groupName))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    @GetMapping("/track-group")
    fun trackGroup(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantListUsers || it.grantGroupManager }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Tankör követése")
        model.addAttribute("description", "A tankörben lévő emberek követése a térképen")
        model.addAttribute("view", "track-group")
        model.addAttribute("columns", trackDescriptor.getColumns())
        model.addAttribute("fields", trackDescriptor.getColumnDefinitions())
        model.addAttribute("rows", locationService.getRecentLocations().map { TrackGroupVirtualEntity(it.groupName) }.distinct())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_TRACK)

        return "overview"
    }

    @GetMapping("/share-location")
    fun shareLocation(model: Model, request: HttpServletRequest): String {

        model.addAttribute("user", request.getUser())
        model.addAttribute("accessToken", request.getUser().cmschId.substring(prefix.length))

        return "shareLocation"
    }

}
