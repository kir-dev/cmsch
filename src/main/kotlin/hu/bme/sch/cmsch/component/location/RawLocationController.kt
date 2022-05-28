package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.CONTROL_MODE_LOCATION
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_LOCATIONS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/locations")
@ConditionalOnBean(LocationComponent::class)
class RawLocationController(
    private val locationService: LocationService,
    private val adminMenuService: AdminMenuService
) {

    private val view = "locations"
    private val titleSingular = "Pozíció"
    private val titlePlural = "Pozíciók"
    private val description = "A helymeghatározás adatai nyersen"
    private val permissionControl = PERMISSION_SHOW_LOCATIONS

    private val overviewDescriptor = OverviewBuilder(LocationEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            LocationComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "place",
                "/admin/control/${view}",
                2,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
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
        model.addAttribute("rows", locationService.findAllLocation())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_LOCATION)

        return "overview"
    }

    @GetMapping("/admin/control/locations/clean")
    fun clean(auth: Authentication): String {
        if (permissionControl.validate(auth.getUser()))
            locationService.clean()
        return "redirect:/admin/control/$view"
    }

    @GetMapping("/admin/control/locations/refresh")
    fun refresh(auth: Authentication): String {
        if (permissionControl.validate(auth.getUser()))
            locationService.refresh()
        return "redirect:/admin/control/$view"
    }

}
