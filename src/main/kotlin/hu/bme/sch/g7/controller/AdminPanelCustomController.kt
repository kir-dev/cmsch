package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dto.TopListEntryDto
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.service.LeaderBoardService
import hu.bme.sch.g7.service.ProductService
import hu.bme.sch.g7.service.RealtimeConfigService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import kotlin.random.Random
import kotlin.random.asJavaRandom

const val CONTROL_MODE_TOPLIST = "toplist"
const val CONTROL_MODE_PAYED = "payed"

@Controller
@RequestMapping("/admin/control")
class AdminPanelCustomController(
        private val leaderBoardService: LeaderBoardService,
        private val productService: ProductService,
        private val config: RealtimeConfigService
) {

    private val topListDescriptor = OverviewBuilder(TopListEntryDto::class)
    private val debtsDescriptor = OverviewBuilder(SoldProductEntity::class)

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    @GetMapping("/basics")
    fun dashboard(model: Model, request: HttpServletRequest): String {
        model.addAttribute("user", request.getUser())
        model.addAttribute("motd", config.getMotd())
        model.addAttribute("website", config.getWebsiteUrl())
        model.addAttribute("staffMessage", config.getStaffMessage())

        return "admin"
    }

    @GetMapping("/toplist")
    fun toplist(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantCreateAchievement }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", "Toplista")
        model.addAttribute("description", "Tankörök helyezése a pontversenyben. " +
                "A pontok 10 percenként számítódnak újra. " +
                "Manuális újrageneráláshoz van egy gomb a lap alján.")
        model.addAttribute("view", "toplist")
        model.addAttribute("columns", topListDescriptor.getColumns())
        model.addAttribute("fields", topListDescriptor.getColumnDefinitions())
        model.addAttribute("rows", leaderBoardService.getBoard())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_TOPLIST)

        return "overview"
    }

    @GetMapping("/toplist/refresh")
    fun refreshTopList(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantCreateAchievement || it.grantCreateAchievement }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        leaderBoardService.recalculate()
        return "redirect:/admin/control/toplist"
    }

    @GetMapping("/debts-of-my-group")
    fun debtsOfMyGroup(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger }?.not() ?: true) {
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

    @GetMapping("/debts-of-my-group/payed/{id}")
    fun setDebtsStatus(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger }?.not() ?: true) {
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
    fun delete(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantGroupDebtsMananger }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        productService.setTransactionPayed(id, request.getUser())
        return "redirect:/admin/control/debts-of-my-group"
    }

}