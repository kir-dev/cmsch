package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.RiddleCategoryDto
import hu.bme.sch.cmsch.dto.RiddleSubmissionDto
import hu.bme.sch.cmsch.dto.view.RiddleHintView
import hu.bme.sch.cmsch.dto.view.RiddleView
import hu.bme.sch.cmsch.component.riddle.RiddleService
import hu.bme.sch.cmsch.riddle.view.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class RiddleApiController(
    private val riddleService: RiddleService
) {

    @GetMapping("/riddle")
    fun riddleCategories(request: HttpServletRequest): List<RiddleCategoryDto> {
        return riddleService.listRiddlesForUser(request.getUser())
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, request: HttpServletRequest): ResponseEntity<RiddleView> {
        return riddleService.getRiddleForUser(request.getUser(), riddleId)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, request: HttpServletRequest): ResponseEntity<RiddleHintView> {
        return riddleService.unlockHintForUser(request.getUser(), riddleId)?.let { ResponseEntity.ok(RiddleHintView(it)) }
            ?: ResponseEntity.notFound().build()
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/{riddleId}")
    fun submitRiddle(
        @PathVariable riddleId: Int, @RequestBody body: RiddleSubmissionDto, request: HttpServletRequest
    ): ResponseEntity<RiddleSubmissionView> {
        return riddleService.submitRiddleForUser(request.getUser(), riddleId, body.solution)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

}
