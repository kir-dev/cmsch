package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.riddle.RiddleService
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(QrFightComponent::class)
class QrFightApiController(
    private val qrFightService: QrFightService,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    @GetMapping("/levels")
    fun getLevels(auth: Authentication?): QrFightOverviewView {
        return when (startupPropertyConfig.tokenOwnershipMode) {
            OwnershipType.USER -> qrFightService.getLevelsForUsers(auth.getUserFromDatabaseOrNull())
            OwnershipType.GROUP -> qrFightService.getLevelsForGroups(auth.getUserFromDatabaseOrNull()?.group)
        }
    }

}
