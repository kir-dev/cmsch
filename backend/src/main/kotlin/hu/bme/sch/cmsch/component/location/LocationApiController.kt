package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(LocationComponent::class)
class LocationApiController(
    private val locationService: LocationService,
    private val locationComponent: LocationComponent
) {

    @PostMapping("/location")
    fun pushLocation(@RequestBody payload: LocationDto): LocationResponse {
        return locationService.pushLocation(payload)
    }

    @GetMapping("/api/track-my-group")
    fun trackMyGroup(auth: Authentication?): List<LocationEntity> {
        val user = auth?.getUserFromDatabaseOrNull() ?: return listOf()
        return locationService.findLocationsOfGroup(user.groupName)
    }


}
