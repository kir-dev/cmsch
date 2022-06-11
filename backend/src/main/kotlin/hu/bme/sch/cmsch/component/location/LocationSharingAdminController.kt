package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_HAS_GROUP
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/share-location")
@ConditionalOnBean(LocationComponent::class)
class LocationSharingAdminController(
    private val adminMenuService: AdminMenuService,
    private val locationComponent: LocationComponent,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val view = "share-location"
    private val titlePlural = "Helymeghatározás"
    private val permissionControl = PERMISSION_IMPLICIT_HAS_GROUP

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            LocationComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "share_location",
                "/admin/control/${view}",
                6,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun shareLocation(model: Model, auth: Authentication): String {
        val user = auth.getUserFromDatabase()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("user", user)
        model.addAttribute("accessToken", user.cmschId.substring(startupPropertyConfig.profileQrPrefix.length))
        model.addAttribute("apkUrl", locationComponent.apkUrl.getValue())

        return "shareLocation"
    }

}
