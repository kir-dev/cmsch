package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(RiddleService::class)
class RiddleApiController(
    private val riddleService: RiddleService,
    private val startupPropertyConfig: StartupPropertyConfig
) {

    @GetMapping("/riddle")
    fun riddleCategories(auth: Authentication?): List<RiddleCategoryDto> {
        val user = auth?.getUserFromDatabaseOrNull() ?: return listOf()
        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.listRiddlesForUser(user)
            OwnershipType.GROUP -> riddleService.listRiddlesForGroup(user, user.group)
        }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, auth: Authentication?): ResponseEntity<RiddleView> {
        val user = auth?.getUserFromDatabaseOrNull() ?: return ResponseEntity.badRequest().build()
        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.getRiddleForUser(user, riddleId)?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.getRiddleForGroup(user, user.group, riddleId)?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, auth: Authentication?): ResponseEntity<RiddleHintView> {
        val user = auth?.getUserFromDatabaseOrNull() ?: return ResponseEntity.badRequest().build()
        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.unlockHintForUser(user, riddleId)
                ?.let { ResponseEntity.ok(RiddleHintView(it)) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.unlockHintForGroup(user, user.group, riddleId)
                ?.let { ResponseEntity.ok(RiddleHintView(it)) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/{riddleId}")
    fun submitRiddle(
        @PathVariable riddleId: Int,
        @RequestBody body: RiddleSubmissionDto,
        auth: Authentication?
    ): ResponseEntity<RiddleSubmissionView> {
        val user = auth?.getUserFromDatabaseOrNull() ?: return ResponseEntity.badRequest().build()
        return when (startupPropertyConfig.riddleOwnershipMode) {
            OwnershipType.USER -> riddleService.submitRiddleForUser(user, riddleId, body.solution)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
            OwnershipType.GROUP -> riddleService.submitRiddleForGroup(user, user.group, riddleId, body.solution)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

}
