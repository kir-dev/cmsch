package hu.bme.sch.cmsch.component.kirpay

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(KirPayComponent::class)
class KirPayApiController(
    private val kirPayService: KirPayService,
    private val kirPayComponent: KirPayComponent
) {

    @JsonView(FullDetails::class)
    @GetMapping("/kirpay-leaderboard")
    fun leaderboard(auth: Authentication?): ResponseEntity<KirPayLeaderboardView> {
        val user = auth?.getUserOrNull()

        if (!kirPayComponent.leaderboardEnabled)
            return ResponseEntity.ok(KirPayLeaderboardView())

        if (!kirPayComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        var entries = kirPayService.getConsumptionLeaderboard()

        val limit = kirPayComponent.leaderboardMaxEntries.toInt()
        if (limit >= 0) {
            entries = entries.take(limit)
        }

        return ResponseEntity.ok(KirPayLeaderboardView(entries = entries))
    }

}
