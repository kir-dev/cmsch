package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
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
@ConditionalOnBean(RiddleComponent::class)
class RiddleApiController(
    private val riddleService: ConcurrentRiddleService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val riddleComponent: RiddleComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/riddle/categories")
    fun riddleCategories(auth: Authentication?): ResponseEntity<List<RiddleCategoryDto>> {
        val user = auth?.getUserOrNull()
            ?: return ResponseEntity.ok(listOf())
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        return ResponseEntity.ok(when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.listRiddlesForUser(user)
            OwnershipType.GROUP -> riddleService.listRiddlesForGroup(user, user.groupId)
        })
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/solve/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, auth: Authentication?): ResponseEntity<RiddleView> {
        val user = auth?.getUserOrNull()
            ?: return ResponseEntity.badRequest().build()
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.getRiddleForUser(user, riddleId)?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.getRiddleForGroup(user, user.groupId, riddleId)?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/solve/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, auth: Authentication?): ResponseEntity<RiddleHintView> {
        val user = auth?.getUserOrNull() ?: return ResponseEntity.badRequest().build()
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.unlockHintForUser(user, riddleId)
                ?.let { ResponseEntity.ok(RiddleHintView(it)) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.unlockHintForGroup(user, user.groupId, user.groupName, riddleId)
                ?.let { ResponseEntity.ok(RiddleHintView(it)) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/solve/{riddleId}/skip")
    fun skipRiddle(@PathVariable riddleId: Int, auth: Authentication?): ResponseEntity<RiddleSubmissionView> {
        val user = auth?.getUserOrNull() ?: return ResponseEntity.badRequest().build()
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        log.info("User '{}' is skipping '{}' riddle id:{}", user.userName, "", riddleId)

        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.submitRiddleForUser(user, riddleId, "", skip = true)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.submitRiddleForGroup(user, user.groupId, user.groupName, riddleId, "", skip = true)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/solve/{riddleId}")
    fun submitRiddle(
        @PathVariable riddleId: Int,
        @RequestBody body: RiddleSubmissionDto,
        auth: Authentication?
    ): ResponseEntity<RiddleSubmissionView> {
        val user = auth?.getUserOrNull()
            ?: return ResponseEntity.badRequest().build()
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        log.info("User '{}' is submitting '{}' for riddle id:{}", user.userName, body.solution, riddleId)

        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.submitRiddleForUser(user, riddleId, body.solution)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.submitRiddleForGroup(user, user.groupId, user.groupName, riddleId, body.solution)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/history")
    fun riddleHistory(auth: Authentication?): ResponseEntity<Map<String, List<RiddleViewWithSolution>>> {
        val user = auth?.getUserOrNull()
            ?: return ResponseEntity.ok(mapOf())
        if (!riddleComponent.minRole.isAvailableForRole(user.role))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        return ResponseEntity.ok(when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.listRiddleHistoryForUser(user)
            OwnershipType.GROUP -> riddleService.listRiddleHistoryForGroup(user, user.groupId)
        })
    }

}
