package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_HAS_GROUP
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabase
import hu.bme.sch.cmsch.util.markdownToHtml
import hu.bme.sch.cmsch.util.urlEncode
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/share-location")
@ConditionalOnBean(LocationComponent::class)
class LocationSharingController(
    private val adminMenuService: AdminMenuService,
    private val locationComponent: LocationComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val auditLogService: AuditLogService,
    private val appComponent: ApplicationComponent,
    private val userService: UserService,
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
                "/admin/control/$view",
                6,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun shareLocation(model: Model, auth: Authentication): String {
        val user = auth.getUserEntityFromDatabase(userService)
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, locationComponent.component, "GET /share-location",
                permissionControl.permissionString)
            return "admin403"
        }
        val accessToken = user.cmschId.substring(startupPropertyConfig.profileQrPrefix.length)
        model.addAttribute("user", user)
        model.addAttribute("accessToken", accessToken)
        model.addAttribute("installGuide", markdownToHtml(locationComponent.installGuide))
        model.addAttribute("androidAppUrl", locationComponent.androidAppUrl)
        model.addAttribute("iosAppUrl", locationComponent.iosAppUrl)
        val apiEndpoint = "${appComponent.adminSiteUrl}api/location"
        val apiEndpointUrlEncoded = apiEndpoint.urlEncode()
        model.addAttribute("appOpenUrl", "cmsch-tracker://?key=${accessToken}&endpoint=${apiEndpointUrlEncoded}")

        return "shareLocation"
    }

}
