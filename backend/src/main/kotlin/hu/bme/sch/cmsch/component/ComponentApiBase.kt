package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.annotation.PostConstruct

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
    private val componentCategory: String = componentClass.simpleName,
    private val menuService: MenuService
) {

    private val log = LoggerFactory.getLogger(javaClass)

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
    fun settings(auth: Authentication, model: Model): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (!permissionToShow.validate(user)) {
            model.addAttribute("permission", permissionToShow.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("component", component.component)
        model.addAttribute("title", componentMenuName)
        model.addAttribute("settings", component.allSettings)
        model.addAttribute("componentNames", menuService.getComponentNames())
        model.addAttribute("user", user)

        return "componentSettings"
    }

    @PostMapping("/settings")
    fun update(auth: Authentication, model: Model, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!permissionToShow.validate(user)) {
            model.addAttribute("permission", permissionToShow.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }
        component.allSettings.forEach { setting ->
            if (setting.type == SettingType.BOOLEAN) {
                val parsedValue = allRequestParams[setting.property] != null && allRequestParams[setting.property] != "off"
                log.info("Changing the value of {}.{} to '{}'", setting.component, setting.property, parsedValue)
                setting.setValue(if (parsedValue) "true" else "false")
            } else {
                allRequestParams[setting.property]?.let {
                    log.info("Changing the value of {}.{} to '{}'", setting.component, setting.property, it)
                    setting.setValue(it)
                }
            }
        }
        component.persistChanges()
        RoleType.values().forEach { role -> menuService.regenerateMenuCache(role) }

        return "redirect:/admin/control/component/${component.component}/settings"
    }

}
