package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.util.getUserOrNull
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(QrFightComponent::class)
class QrFightApiController(
    private val qrFightComponent: QrFightComponent,
    private val qrFightService: QrFightService,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/levels")
    fun getLevels(auth: Authentication?): QrFightOverviewView {
        val user = auth.getUserOrNull()
        return when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> qrFightService.getLevelsForUsers(user)
            OwnershipType.GROUP -> qrFightService.getLevelsForGroups(user?.groupId, user?.groupName ?: "")
        }
    }

    @GetMapping("/qrfight/tower/{selector}")
    fun updateValidSignups(
        @RequestParam(defaultValue = "none") token: String,
        @PathVariable selector: String
    ): ResponseEntity<QrFightTowerDto> {
        if (token == "none") {
            log.info("[QRFIGHT] No token presents")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        if (!qrFightComponent.apiTokens.getValue().split(", *").contains("$selector:$token")) {
            log.info("[QRFIGHT] Invalid token '$selector:$token'")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(QrFightTowerDto())
        }

        log.info("[QRFIGHT] Fetched {} by {}", selector, token)
        return ResponseEntity.ok(qrFightService.getTowerDetails(selector))
    }
}
