package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(RaceComponent::class)
class RaceApiController(
    private val raceService: RaceService,
    private val raceComponent: RaceComponent,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    @JsonView(FullDetails::class)
    @GetMapping("/race")
    fun race(auth: Authentication?): ResponseEntity<RaceView> {
        val user = auth?.getUserOrNull()

        if (!raceComponent.visible.isValueTrue())
            return ResponseEntity.ok(RaceView())

        if (!raceComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.USER -> ResponseEntity.ok(raceService.getViewForUsers(user))
            OwnershipType.GROUP -> ResponseEntity.ok(raceService.getViewForGroups(user))
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/race/{category}")
    fun raceByCategory(@PathVariable category: String, auth: Authentication?): ResponseEntity<RaceView> {
        val user = auth?.getUserOrNull()

        if (!raceComponent.extraCategoriesVisible.isValueTrue())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        if (!raceComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return try {
            when (startupPropertyConfig.raceOwnershipMode) {
                OwnershipType.USER -> ResponseEntity.ok(raceService.getViewForUsers(user, category))
                OwnershipType.GROUP -> ResponseEntity.ok(raceService.getViewForGroups(user, category))
            }
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/freestyle-race")
    fun freestyleRace(auth: Authentication?): ResponseEntity<FreestyleRaceView> {
        val user = auth?.getUserOrNull()

        if (!raceComponent.visible.isValueTrue())
            return ResponseEntity.ok(FreestyleRaceView())

        if (!raceComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.USER -> ResponseEntity.ok(raceService.getFreestyleViewForUsers())
            OwnershipType.GROUP -> ResponseEntity.ok(raceService.getFreestyleViewForGroups())
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/race-by-team/{teamId}")
    fun raceByTeam(auth: Authentication?, @PathVariable teamId: Int): ResponseEntity<RaceView> {
        val user = auth?.getUserOrNull()

        if (!raceComponent.visible.isValueTrue())
            return ResponseEntity.ok(RaceView())

        if (!raceComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.GROUP -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            OwnershipType.USER -> ResponseEntity.ok(raceService.getRaceByTeam(teamId, user?.id ?: 0))
        }
    }

}
