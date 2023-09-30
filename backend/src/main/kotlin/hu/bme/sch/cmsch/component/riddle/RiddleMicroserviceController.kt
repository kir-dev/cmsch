package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.CMSCH_VERSION
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.*

private const val INVALID_TOKEN = "invalid-token"
private const val OK = "ok"
private const val DISABLED = "ok"

@RestController
@ConditionalOnBean(RiddleComponent::class)
@RequestMapping("/remote-api/riddle")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class RiddleMicroserviceController(
    private val riddleComponent: RiddleComponent,
    private val riddleCacheManager: RiddleCacheManager,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val env: Environment
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/reload-component-config")
    fun reloadComponentConfig(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: reloadComponentConfig")
        riddleComponent.updateFromDatabase()
        return OK
    }

    @PostMapping("/reload-riddle-and-category-cache")
    fun reloadRiddleAndCategoryCache(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: reloadRiddleAndCategoryCache")
        riddleCacheManager.resetCache(persistMapping = false, overrideMappings = false)
        riddleComponent.updateBanLists()
        return OK
    }

    @PostMapping("/reload-all")
    fun reloadAll(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: reloadAll")
        riddleCacheManager.resetCache(persistMapping = false, overrideMappings = true)
        return OK
    }

    @PostMapping("/save-all")
    fun saveAll(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: saveAll")
        riddleCacheManager.resetCache(persistMapping = true, overrideMappings = true)
        return OK
    }

    @PostMapping("/force-unlock-everything")
    fun forceUnlockEverything(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: forceUnlockEverything")
        riddleCacheManager.forceUnlock()
        return OK
    }

    @PostMapping("/ping")
    fun ping(@RequestHeader token: String): String {
        if (!startupPropertyConfig.riddleMicroserviceEnabled) {
            return DISABLED
        }
        if (token != startupPropertyConfig.managementToken) {
            return INVALID_TOKEN
        }
        log.info("Calling remote command: ping")
        riddleComponent.allSettings.forEach {
            log.info("${it.component}.${it.property} = ${it.getValue()}")
        }
        log.info("cmsch.frontend.production-url = ${env.getProperty("cmsch.frontend.production-url")}")
        return "PONG ${startupPropertyConfig.nodeName} ${CMSCH_VERSION}"
    }

}