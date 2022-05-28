package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(RiddleService::class)
class RiddleApiController(
    private val riddleService: RiddleService
) {

    @GetMapping("/riddle")
    fun riddleCategories(auth: Authentication): List<RiddleCategoryDto> {
        return riddleService.listRiddlesForUser(auth.getUser())
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, auth: Authentication): ResponseEntity<RiddleView> {
        return riddleService.getRiddleForUser(auth.getUser(), riddleId)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, auth: Authentication): ResponseEntity<RiddleHintView> {
        return riddleService.unlockHintForUser(auth.getUser(), riddleId)?.let { ResponseEntity.ok(RiddleHintView(it)) }
            ?: ResponseEntity.notFound().build()
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/{riddleId}")
    fun submitRiddle(
        @PathVariable riddleId: Int, @RequestBody body: RiddleSubmissionDto, auth: Authentication
    ): ResponseEntity<RiddleSubmissionView> {
        return riddleService.submitRiddleForUser(auth.getUser(), riddleId, body.solution)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

}
