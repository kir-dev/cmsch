package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.util.getUserEntityFromDatabase
import hu.bme.sch.cmsch.util.urlEncode
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/redirect")
@ConditionalOnBean(LocationComponent::class)
class LocationDeepLinkController(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val appComponent: ApplicationComponent
) {

    @GetMapping("/beacon")
    fun beaconRedirectUrl(auth: Authentication?): String {
        if (auth == null)
            return "redirect:/control/login"

        val user = auth.getUserEntityFromDatabase()
        val accessToken = user.cmschId.substring(startupPropertyConfig.profileQrPrefix.length)
        val apiEndpoint = "${appComponent.adminSiteUrl.getValue()}api/location"

        return "redirect:cmsch-tracker://?key=${accessToken}&endpoint=${apiEndpoint.urlEncode()}"
    }

}
