package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.token.TokenCollectorGroupVirtualEntity
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_PDF
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/menu")
@ConditionalOnBean(ApplicationComponent::class)
class MenuAdminController(
    private val adminMenuService: AdminMenuService,
    private val menuRepository: MenuRepository
) {

    private val view = "menu"
    private val titleSingular = "Menü beállítások"
    private val titlePlural = "Menü beállítások"
    private val description = "Egyes roleokkal hogy nézzen ki a menü"
    private val permissionControl = ControlPermissions.PERMISSION_CONTROL_APP

    private val overviewDescriptor = OverviewBuilder(MenuSetupByRoleVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "menu_open",
                "/admin/control/${view}",
                2,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        return "overview"
    }

    private fun fetchOverview(): List<MenuSetupByRoleVirtualEntity> {
        return RoleType.values()
            .filter { it.value < 10000 }
            .map { MenuSetupByRoleVirtualEntity(it.ordinal, it.name, it.description) }
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }
        val role = RoleType.values()[id]
//        model.addAttribute("user", menu.)

        return "menuSettings"
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
