package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesTinderApiController(
    private val tinderService: TinderService,
    private val userService: UserService
) {

    @GetMapping("/tinder/question")
    fun allQuestions(): List<TinderQuestionDto> {
        return tinderService.getAllQuestions().map{ TinderQuestionDto(it) }
    }

    @GetMapping("/tinder/question/answers")
    fun getAnswers(auth: Authentication?): TinderAnswerStatus {
        val user = auth?.getUserOrNull() ?: return TinderAnswerStatus()
        val answer: TinderAnswerDto = tinderService.getAnswerDtoForUser(user.id)
            .getOrElse{return TinderAnswerStatus() }
        return TinderAnswerStatus(answered = true, answer = answer)
    }

    @PostMapping("/tinder/question/anwers")
    fun submitAnswers(
        auth: Authentication?,
        @RequestBody answers: TinderAnswerDto
    ): TinderAnswerResponse {
        val user = auth?.getUserOrNull()
            ?: return TinderAnswerResponse(TinderAnswerResponseStatus.NO_PERMISSION)
        return TinderAnswerResponse(tinderService.submitAnswers(false, user, answers))
    }

    @PutMapping("/tinder/question/answers")
    fun updateAnswers(
        auth: Authentication?,
        @RequestBody answers: TinderAnswerDto
    ): TinderAnswerResponse {
        val user = auth?.getUserOrNull()
            ?: return TinderAnswerResponse(TinderAnswerResponseStatus.NO_PERMISSION)
        return TinderAnswerResponse(tinderService.submitAnswers(true, user, answers))
    }

    @GetMapping("tinder/community")
    fun getTinderCommunities(auth: Authentication?): List<CommunitiesTinderDto> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return emptyList()
        return tinderService.getTinderCommunities(user)
    }

    @PostMapping("tinder/community/interact")
    fun interactWithCommunity(
        auth: Authentication?,
        @RequestBody interaction: TinderInteractionDto
    ): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return false
        return tinderService.interactWithCommunity(user, interaction)
    }

}