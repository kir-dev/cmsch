package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_EVERYBODY
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_ONE_GROUP
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.PostConstruct

@Controller
@ConditionalOnBean(LocationComponent::class)
class TrackingMapController(
    private val locationService: LocationService,
    private val adminMenuService: AdminMenuService
) {

    @ResponseBody
    @GetMapping("/api/track")
    fun api(auth: Authentication): List<LocationEntity> {
        if (PERMISSION_TRACK_EVERYBODY.validate(auth.getUser()).not()) {
            return listOf()
        }
        return locationService.findAllLocation()
    }

    @ResponseBody
    @GetMapping("/api/track/{groupName}")
    fun apiGroup(@PathVariable groupName: String, auth: Authentication): List<LocationEntity> {
        if (PERMISSION_TRACK_ONE_GROUP.validate(auth.getUser()).not()) {
            return listOf()
        }
        return locationService.findLocationsOfGroup(groupName)
    }

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            LocationComponent::class.simpleName!!, AdminMenuEntry(
                "Követés (térkép)",
                "map",
                "/admin/control/tracking",
                3,
                PERMISSION_TRACK_EVERYBODY
            )
        )
    }

    @GetMapping("/admin/control/tracking")
    fun view(auth: Authentication, model: Model): String {
        val user = auth.getUser()
        if (PERMISSION_TRACK_EVERYBODY.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_TRACK_EVERYBODY.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("url", "/api/track")
        return "tracker"
    }

    @GetMapping("/admin/control/tracking/{groupName}")
    fun viewGroup(@PathVariable groupName: String, auth: Authentication, model: Model): String {
        val user = auth.getUser()
        if (PERMISSION_TRACK_ONE_GROUP.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_TRACK_ONE_GROUP.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("url", "/api/track/${groupName}")
        return "tracker"
    }

}
