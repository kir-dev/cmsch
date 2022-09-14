package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_TOPLIST
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_LEADERBOARD
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_IMPORT_EXPORT
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_LEADERBOARD
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin/control")
@ConditionalOnBean(LeaderBoardComponent::class)
class LeaderBoardShowAdminController(
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
                "Csoport toplista",
                "leaderboard",
                "/admin/control/group-toplist",
                2,
                permissionControlShow
            )
        )
    }

    @GetMapping("/user-toplist")
    fun userToplist(model: Model, auth: Authentication): String {
        val user = auth.getUser()
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
        model.addAttribute("savable", true)

        return "overview"
    }

    @GetMapping("/user-toplist/refresh")
    fun refreshUserTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardService.forceRecalculateForUsers()
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-enable")
    fun enableRefreshUserTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("true")
        return "redirect:/admin/control/user-toplist"
    }

    @GetMapping("/user-toplist/refresh-disable")
    fun disableRefreshUserTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardEnabled.setAndPersistValue("false")
        return "redirect:/admin/control/user-toplist"
    }

    @ResponseBody
    @GetMapping("/user-toplist/save/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun saveUserAsCsv(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"user-toplist-export.csv\"")
        return userTopListDescriptor.exportToCsv(leaderBoardService.getBoardAnywaysForUsers()).toByteArray()
    }

    @GetMapping("/group-toplist")
    fun groupToplist(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControlShow.validate(user).not()) {
            model.addAttribute("permission", permissionControlShow.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", "Csoport Toplista")
        model.addAttribute("description", "Csoportok helyezése a pontversenyben. " +
                "A pontok 10 percenként számítódnak újra. " +
                "Manuális újrageneráláshoz van egy gomb a lap alján.")
        model.addAttribute("view", "group-toplist")
        model.addAttribute("columns", groupTopListDescriptor.getColumns())
        model.addAttribute("fields", groupTopListDescriptor.getColumnDefinitions())
        model.addAttribute("rows", leaderBoardService.getBoardAnywaysForGroups())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", if (permissionControlShow.validate(user)) CONTROL_MODE_TOPLIST else CONTROL_MODE_NONE)
        model.addAttribute("leaderboardEnabled", leaderBoardComponent.leaderboardEnabled.isValueTrue())
        model.addAttribute("leaderboardUpdates", !leaderBoardComponent.leaderboardFrozen.isValueTrue())
        model.addAttribute("savable", true)

        return "overview"
    }

    @GetMapping("/group-toplist/refresh")
    fun refreshGroupTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardService.forceRecalculateForGroups()
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-enable")
    fun enableRefreshGroupTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("false")
        return "redirect:/admin/control/group-toplist"
    }

    @GetMapping("/group-toplist/refresh-disable")
    fun disableRefreshGroupTopList(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControlControl.validate(user).not()) {
            model.addAttribute("permission", permissionControlControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        leaderBoardComponent.leaderboardFrozen.setAndPersistValue("true")
        return "redirect:/admin/control/group-toplist"
    }

    @ResponseBody
    @GetMapping("/group-toplist/save/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun saveGroupAsCsv(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"group-toplist-export.csv\"")
        return groupTopListDescriptor.exportToCsv(leaderBoardService.getBoardAnywaysForGroups()).toByteArray()
    }


}
