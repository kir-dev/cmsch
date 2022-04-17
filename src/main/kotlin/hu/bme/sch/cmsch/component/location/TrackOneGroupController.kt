package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_TRACK
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_ONE_GROUP
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/track-group")
@ConditionalOnBean(LocationComponent::class)
class TrackOneGroupController(
    private val locationService: LocationService,
    private val adminMenuService: AdminMenuService
) {

    private val view = "track-group"
    private val titlePlural = "Tankör követése"
    private val permissionControl = PERMISSION_TRACK_ONE_GROUP
    private val trackDescriptor = OverviewBuilder(TrackGroupVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            LocationComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "my_location",
                "/admin/control/${view}",
                4,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun trackGroup(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("description", "A tankörben lévő emberek követése a térképen")
        model.addAttribute("view", view)
        model.addAttribute("columns", trackDescriptor.getColumns())
        model.addAttribute("fields", trackDescriptor.getColumnDefinitions())
        model.addAttribute("rows", locationService.getRecentLocations().map { TrackGroupVirtualEntity(it.groupName) }.distinct())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_TRACK)

        return "overview"
    }

}
