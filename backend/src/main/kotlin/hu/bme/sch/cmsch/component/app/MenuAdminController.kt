package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/menu")
@ConditionalOnBean(ApplicationComponent::class)
class MenuAdminController(
    private val adminMenuService: AdminMenuService,
    private val menuRepository: MenuRepository,
    private val menuService: MenuService,
    private val objectMapper: ObjectMapper
) {

    private val view = "menu"
    private val titleSingular = "Menü beállítások"
    private val titlePlural = "Menü beállítások"
    private val description = "Egyes roleokkal hogy nézzen ki a menü"
    private val showPermission = ControlPermissions.PERMISSION_CONTROL_APP
    private val editPermission = ControlPermissions.PERMISSION_CONTROL_APP

    private val overviewDescriptor = OverviewBuilder(MenuSetupByRoleVirtualEntity::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.CONTENT_CATEGORY, AdminMenuEntry(
                titlePlural,
                "menu_open",
                "/admin/control/${view}",
                3,
                showPermission
            )
        )
        controlActions.add(
            ControlAction(
                "Megnyitás",
                "edit/{id}",
                "edit",
                editPermission,
                100
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", overviewDescriptor.getColumnsAsJson())
        model.addAttribute("tableData", overviewDescriptor.getTableDataAsJson(fetchOverview()))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", overviewDescriptor.toJson(
            controlActions.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("buttonActions", listOf<ButtonAction>())

        return "overview4"
    }

    private fun fetchOverview(): List<MenuSetupByRoleVirtualEntity> {
        return RoleType.values()
            .filter { it.value < 10000 }
            .map { MenuSetupByRoleVirtualEntity(it.ordinal, it.name, it.description) }
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }
        val role = RoleType.values()[id]
        model.addAttribute("id", id)
        model.addAttribute("role", role)
        model.addAttribute("rows", menuService.getMenusForRole(role).sortedBy { it.order })

        return "menuSettings"
    }

    @PostMapping("/edit/{id}")
    fun editFormTarget(@PathVariable id: Int, auth: Authentication, model: Model, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val menus = mutableListOf<MenuSettingItem>()
        var i = 0
        while (allRequestParams["id_$i"] != null && allRequestParams["order_$i"] != null) {
            menus.add(MenuSettingItem(id = allRequestParams["id_$i"]!!,
                name ="", url = "",
                order = allRequestParams["order_$i"]?.toIntOrNull() ?: 0,
                visible = allRequestParams["visible_$i"] != null && allRequestParams["visible_$i"] != "off",
                subMenu = allRequestParams["submenu_$i"] != null && allRequestParams["submenu_$i"] != "off"
            ))
            i++
        }
        menuService.persistSettings(menus, RoleType.values()[id])
        return "redirect:/admin/control/menu/edit/$id"
    }
}

class MenuSetupByRoleVirtualEntity(
    @property:GenerateOverview(visible = false)
    val id: Int,

    @property:GenerateOverview(columnName = "Role", order = 1)
    val role: String,

    @property:GenerateOverview(columnName = "Leírás", order = 2)
    val description: String,
)
