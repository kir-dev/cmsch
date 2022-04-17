package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_TOPLIST
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PERMISSION_CONTROL_LEADERBOARD
import hu.bme.sch.cmsch.service.PERMISSION_SHOW_LEADERBOARD
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control")
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardAdminController(
    private val leaderBoardService: LeaderBoardService,
    private val leaderBoardComponent: LeaderBoardComponent,
    private val adminMenuService: AdminMenuService
) {

    private val groupTopListDescriptor = OverviewBuilder(TopListAsGroupEntryDto::class)
    private val userTopListDescriptor = OverviewBuilder(TopListAsUserEntryDto::class)

    private val permissionControlShow = PERMISSION_SHOW_LEADERBOARD
    private val permissionControlControl = PERMISSION_CONTROL_LEADERBOARD

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            LeaderBoardComponent::class.simpleName!!, AdminMenuEntry(
                "Felhasználói toplista",
                "leaderboard",
                "/admin/control/user-toplist",
                1,
                permissionControlShow
            )
        )

        adminMenuService.registerEntry(
            LeaderBoardComponent::class.simpleName!!, AdminMenuEntry(
                "Tankör toplista",
                "leaderboard",
                "/admin/control/group-toplist",
                2,
                permissionControlShow
            )
        )
    }

    @GetMapping("/user-toplist")
    fun userToplist(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControlShow.validate(user).not()) {
            model.addAttribute("permission", permissionControlShow.permissionString)
            model.addAttribute("user", user)
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
        model.addAttribute("user", user)
        model.addAttribute("controlMode", if (permissionControlShow.validate(user)) CONTROL_MODE_TOPLIST else CONTROL_MODE_NONE)
        model.addAttribute("leaderboardEnabled", leaderBoardComponent.leaderboardEnabled.isValueTrue())
        model.addAttribute("leaderboardUpdates", !leaderBoardComponent.leaderboardFrozen.isValueTrue())

        return "overview"
    }

    @GetMapping("/user-toplist/refresh")
    fun refreshUserTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardService.forceRecalculateForUsers()
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-enable")
    fun enableRefreshUserTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("true")
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-disable")
    fun disableRefreshUserTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("false")
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/group-toplist")
    fun groupToplist(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControlShow.validate(user).not()) {
            model.addAttribute("permission", permissionControlShow.permissionString)
            model.addAttribute("user", user)
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
        model.addAttribute("controlMode", if (permissionControlShow.validate(user)) CONTROL_MODE_TOPLIST else CONTROL_MODE_NONE)
        model.addAttribute("leaderboardEnabled", leaderBoardComponent.leaderboardEnabled.isValueTrue())
        model.addAttribute("leaderboardUpdates", !leaderBoardComponent.leaderboardFrozen.isValueTrue())

        return "overview"
    }

    @GetMapping("/group-toplist/refresh")
    fun refreshGroupTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardService.forceRecalculateForGroups()
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-enable")
    fun enableRefreshGroupTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("false")
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-disable")
    fun disableRefreshGroupTopList(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("true")
        return "redirect:/admin/control/group-toplist"
    }

}
