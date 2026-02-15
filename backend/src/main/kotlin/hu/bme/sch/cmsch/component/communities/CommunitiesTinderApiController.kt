package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUserEntityFromDatabaseOrNull
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
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
        val answer: Map<String, String> = tinderService.getAnswerMapForUser(user.id)
            .getOrElse{return TinderAnswerStatus() }
        return TinderAnswerStatus(answered = true, answer = answer)
    }

    @PostMapping("/tinder/question/answers")
    fun submitAnswers(
        @RequestBody data: Map<String, String>,
        auth: Authentication?
    ): TinderAnswerResponseStatus {
        val user = auth?.getUserOrNull()
            ?: return TinderAnswerResponseStatus.NO_PERMISSION
        return tinderService.submitAnswers(false, user, data)
    }

    @PutMapping("/tinder/question/answers")
    fun updateAnswers(
        @RequestBody answers: Map<String, String>,
        auth: Authentication?
    ): TinderAnswerResponseStatus {
        val user = auth?.getUserOrNull()
            ?: return TinderAnswerResponseStatus.NO_PERMISSION
        return tinderService.submitAnswers(true, user, answers)
    }

    @GetMapping("/tinder/community")
    fun getTinderCommunities(auth: Authentication?): List<CommunitiesTinderDto> {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return emptyList()
        return tinderService.getTinderCommunities(user)
    }

    @PostMapping("/tinder/community/interact")
    fun interactWithCommunity(
        auth: Authentication?,
        @RequestBody interaction: TinderInteractionDto
    ): Boolean {
        val user = auth?.getUserEntityFromDatabaseOrNull(userService) ?: return false
        return tinderService.interactWithCommunity(user, interaction)
    }

}