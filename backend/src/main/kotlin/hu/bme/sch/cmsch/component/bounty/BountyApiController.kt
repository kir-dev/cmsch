package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@ConditionalOnBean(BountyComponent::class)
class BountyApiController(
    private val bountyService: BountyService,
    private val bountyComponent: BountyComponent
) {

    @GetMapping("/bounty")
    fun state(auth: Authentication?): ResponseEntity<BountyView> {
        val user = auth.getUserOrNull()
        if (!isAvailable(user))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        return ResponseEntity.ok(bountyService.getState(user))
    }

    @PostMapping("/bounty/kill")
    fun kill(@RequestBody request: BountyKillRequest, auth: Authentication?): ResponseEntity<BountyKillResponse> {
        val user = auth.getUserOrNull()
        if (!isAvailable(user))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        if (user == null)
            return ResponseEntity.ok(BountyKillResponse(false, "Nincs csapatod"))
        return ResponseEntity.ok(bountyService.kill(request.code, user))
    }

    private fun isAvailable(user: CmschUser?): Boolean =
        bountyComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST)

}
