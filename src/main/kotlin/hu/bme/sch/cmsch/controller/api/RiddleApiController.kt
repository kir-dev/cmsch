package hu.bme.sch.cmsch.controller.api

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.dto.view.*
import hu.bme.sch.cmsch.model.RiddleCategoryEntity
import hu.bme.sch.cmsch.model.RoleType
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
        return RiddleCategoryView(listOf(
            RiddleCategoryEntity(0, "Általános", 1, true, RoleType.BASIC),
            RiddleCategoryEntity(0, "Elborult", 2, true, RoleType.BASIC),
            RiddleCategoryEntity(0, "Kir-Dev special", 3, true, RoleType.BASIC),
            RiddleCategoryEntity(0, "Ki a Károly? (Simonyi)", 4, true, RoleType.BASIC),
        ))
    }

    @JsonView(FullDetails::class)
    @GetMapping("/riddle/{riddleId}")
    fun riddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleView {
        return RiddleView("/cdn/mockimage.png", "Feledat neve", "Itt csak akkor megy le ha már egyszer lekérte", false)
    }

    @JsonView(FullDetails::class)
    @PutMapping("/riddle/{riddleId}/hint")
    fun hintForRiddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleHintView {
        return RiddleHintView("A hintőpor az egy hint, change my mind")
    }

    @JsonView(FullDetails::class)
    @PostMapping("/riddle/{riddleId}")
    fun submitRiddle(@PathVariable riddleId: Int, request: HttpServletRequest): RiddleSubmissionView {
        return RiddleSubmissionView(status = RiddleSubmissionStatus.CORRECT, 43)
    }

}
