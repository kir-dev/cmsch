package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.RiddleCategoryView
import hu.bme.sch.cmsch.dto.view.RiddleHintView
import hu.bme.sch.cmsch.dto.view.RiddleSubmissionView
import hu.bme.sch.cmsch.dto.view.RiddleView
import hu.bme.sch.cmsch.service.RiddleService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
class RiddleApiController(
    private val riddleService: RiddleService
) {

    @JsonView(Preview::class)
    @GetMapping("/riddle")
    fun riddleCategories(request: HttpServletRequest): RiddleCategoryView {
        return RiddleCategoryView()
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleView {
        return RiddleView()
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleHintView {
        return RiddleHintView()
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/{riddleId}")
    fun submitRiddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleSubmissionView {
        return RiddleSubmissionView()
    }

}
