package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_EVERYBODY
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_ONE_GROUP
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@ConditionalOnBean(LocationComponent::class)
class TrackingMapController(
    private val locationService: LocationService,
    private val adminMenuService: AdminMenuService,
    private val locationComponent: LocationComponent,
    private val auditLogService: AuditLogService
) {

    @ResponseBody
    @GetMapping("/api/track")
    fun api(auth: Authentication?): List<LocationEntity> {
        val user = auth?.getUser() ?: return listOf()
        if (PERMISSION_TRACK_EVERYBODY.validate(user).not()) {
            return listOf()
        }
        return locationService.findAllLocation()
    }

    @ResponseBody
    @GetMapping("/api/track/{groupId}")
    fun apiGroup(@PathVariable groupId: Int, auth: Authentication?): List<LocationEntity> {
        val user = auth?.getUser() ?: return listOf()
        if (PERMISSION_TRACK_ONE_GROUP.validate(user).not()) {
            return listOf()
        }
        return locationService.findLocationsOfGroup(groupId)
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
            adminMenuService.addPartsForMenu(user, model)
            model.addAttribute("permission", PERMISSION_TRACK_EVERYBODY.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, locationComponent.component, "GET /tracking",
                PERMISSION_TRACK_ONE_GROUP.permissionString)
            return "admin403"
        }

        model.addAttribute("url", "/api/track")
        attachComponentProperties(model)
        return "tracker"
    }

    @GetMapping("/admin/control/tracking/{groupId}")
    fun viewGroup(@PathVariable groupId: Int, auth: Authentication, model: Model): String {
        val user = auth.getUser()
        if (PERMISSION_TRACK_ONE_GROUP.validate(user).not()) {
            adminMenuService.addPartsForMenu(user, model)
            model.addAttribute("permission", PERMISSION_TRACK_ONE_GROUP.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, locationComponent.component, "GET /tracking/$groupId",
                PERMISSION_TRACK_ONE_GROUP.permissionString)
            return "admin403"
        }

        model.addAttribute("url", "/api/track/${groupId}")
        attachComponentProperties(model)
        return "tracker"
    }

    private fun attachComponentProperties(model: Model) {
        model.addAttribute("defaultGroupColor", locationComponent.defaultGroupColor)
        model.addAttribute("blackGroupName", locationComponent.blackGroupName)
        model.addAttribute("blueGroupName", locationComponent.blueGroupName)
        model.addAttribute("cyanGroupName", locationComponent.cyanGroupName)
        model.addAttribute("pinkGroupName", locationComponent.pinkGroupName)
        model.addAttribute("orangeGroupName", locationComponent.orangeGroupName)
        model.addAttribute("greenGroupName", locationComponent.greenGroupName)
        model.addAttribute("redGroupName", locationComponent.redGroupName)
        model.addAttribute("whiteGroupName", locationComponent.whiteGroupName)
        model.addAttribute("yellowGroupName", locationComponent.yellowGroupName)
        model.addAttribute("purpleGroupName", locationComponent.purpleGroupName)
        model.addAttribute("grayGroupName", locationComponent.grayGroupName)
    }

}
