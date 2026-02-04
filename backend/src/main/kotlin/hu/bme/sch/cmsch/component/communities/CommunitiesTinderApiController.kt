package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesTinderApiController(
    private val tinderService: TinderService,
    private val userService: UserService
) {

    @RequestMapping("/tinder/question")
    fun allQuestions(): List<TinderQuestionDto> {
        return tinderService.getAllQuestions().map{ TinderQuestionDto(it) }
    }

    @PostMapping("/tinder/question")
    fun submitAnswers(
        auth: Authentication?,
        @RequestBody answers: TinderAnswerDto
    ): ResponseEntity<Unit> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return ResponseEntity.status(401).build()
        tinderService.submitAnswers(false, user, answers)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/tinder/question")
    fun updateAnswers(
        auth: Authentication?,
        @RequestBody answers: TinderAnswerDto
    ): ResponseEntity<Unit> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return ResponseEntity.status(401).build()
        tinderService.submitAnswers(true, user, answers)
        return ResponseEntity.ok().build()
    }

    @GetMapping("tinder/communities")
    fun getTinderCommunities(auth: Authentication?): ResponseEntity<List<CommunitiesTinderDto>> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return ResponseEntity.status(401).build()
        val res = tinderService.getTinderCommunities(user)
        return ResponseEntity.ok(res)
    }

    @PostMapping("tinder/communities/interact")
    fun interactWithCommunity(
        auth: Authentication?,
        @RequestBody interaction: TinderInteractionDto
    ): ResponseEntity<Unit> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return ResponseEntity.status(401).build()
        tinderService.interactWithCommunity(user, interaction)
        return ResponseEntity.ok().build()
    }

}