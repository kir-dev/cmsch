package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dto.TopListEntryDto
import hu.bme.sch.g7.service.LeaderBoardService
import hu.bme.sch.g7.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_TOPLIST = "toplist"

@Controller
@RequestMapping("/admin/control")
class AdminPanelCustomController(
        val leaderBoardService: LeaderBoardService
) {

    private val topListDescriptor = OverviewBuilder(TopListEntryDto::class)

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    @GetMapping("/basics")
    fun dashboard(model: Model, request: HttpServletRequest): String {
        model.addAttribute("user", request.getUser())
        return "admin"
    }

    @GetMapping("/toplist")
    fun toplist(model: Model, request: HttpServletRequest): String {
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
    fun refreshTopList(): String {
        leaderBoardService.recalculate()
        return "redirect:/admin/control/toplist"
    }

}