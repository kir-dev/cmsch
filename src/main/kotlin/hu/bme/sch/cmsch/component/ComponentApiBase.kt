package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.util.getUser
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

abstract class ComponentApiBase(
    val adminMenuService: AdminMenuService,
    private val componentClass: Class<*>,
    val component: ComponentBase,
    private val permissionToShow: PermissionValidator,
    private val componentCategoryName: String = "",
    private val componentMenuName: String,
    private val componentMenuIcon: String = "settings",
    private val componentMenuPriority: Int = 100,
    private val insertComponentCategory: Boolean = true,
    private val componentCategory: String = componentClass.simpleName
) {

    @PostConstruct
    fun init() {
        if (insertComponentCategory) {
            adminMenuService.registerCategory(
                componentCategory,
                AdminMenuCategory(componentCategoryName, component.menuPriority)
            )
        }
        adminMenuService.registerEntry(
            componentCategory, AdminMenuEntry(
                componentMenuName, componentMenuIcon,
                "/admin/control/component/${component.component}/settings",
                componentMenuPriority,
                permissionToShow
            )
        )
    }

    @GetMapping("/settings")
    fun settings(request: HttpServletRequest, model: Model): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (!permissionToShow.validate(user)) {
            model.addAttribute("permission", permissionToShow.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", componentMenuName)
        model.addAttribute("settings", component.allSettings)

        return "componentSettings"
    }

    @ResponseBody
    @PostMapping("/settings")
    fun update(): String {
        return "not-implemented"
    }

}
