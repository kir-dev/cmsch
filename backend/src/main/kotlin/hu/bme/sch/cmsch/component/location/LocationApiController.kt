package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.profile.ProfileComponent
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(LocationComponent::class)
class LocationApiController(
    private val locationService: LocationService,
    private val profileComponent: Optional<ProfileComponent>
) {

    @CrossOrigin(origins = ["*"])
    @PostMapping("/location")
    fun pushLocation(@RequestBody payload: LocationDto): LocationResponse {
        return locationService.pushLocation(payload)
    }

    @CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
    @GetMapping("/track-my-group")
    fun trackMyGroup(auth: Authentication?): List<MapMarker> {
        val user = auth?.getUserOrNull() ?: return listOf()
        if (user.groupName.isEmpty())
            return listOf()
        if (!profileComponent.map { it.showGroupLeadersLocations.isValueTrue() }.orElse(false))
            return listOf()
        return locationService.findLocationsOfGroupName(user.groupName)
    }

}
