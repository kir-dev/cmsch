package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import kotlin.collections.set


@Service
@ConditionalOnBean(CommunitiesComponent::class)
class TinderService(
    private val communityRepository: CommunityRepository,
    private val questionRepository: TinderQuestionRepository,
    private val answerRepository: TinderAnswerRepository,
    private val objectMapper: ObjectMapper,
    private val tinderInteractionRepository: TinderInteractionRepository
) {

    fun getAllQuestions() = questionRepository.findAll().toList()

    fun submitAnswers(update: Boolean, user: UserEntity, answers: TinderAnswerDto) {
        val questions = questionRepository.findAll().associateBy { it.id }
        for (answer in answers.answers) {
            val question = questions[answer.key] ?: continue
            if (!question.answerOptions.split("\n").contains(answer.value)) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong answer option: ${answer.value}")
            }
        }

        val existing = answerRepository.findByUserId(user.id)
        if (!update) {
            if (existing.isPresent){
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Answers already submitted")
            }
            val entity = TinderAnswerEntity(
                userId = user.id,
                communityId = null,
                answers = objectMapper.writeValueAsString(answers.answers)
            )
            answerRepository.save(entity)
        } else {
            val entity = existing.orElseThrow {
                ResponseStatusException(HttpStatus.BAD_REQUEST, "No existing answers to update")
            }
            val answer = mutableMapOf<Int, String>()
            objectMapper.readerFor(object : TypeReference<Map<Int, String>>() {})
                .readValue<Map<Int, String>>(entity.answers)
                .entries
                .forEach {
                    answer[it.key] = it.value
                }
            for (ans in answers.answers) {
                answer[ans.key] = ans.value
            }
            entity.answers = objectMapper.writeValueAsString(answers.answers)
            answerRepository.save(entity)
        }
    }

    fun getTinderCommunities(user: UserEntity): List<CommunitiesTinderDto> {
        val communities = communityRepository.findAll()
        val answerList = answerRepository.findAllWithCommunityIdNotNull()
        val answers = mutableMapOf<Int, Map<Int, String>>()
        for (answer in answerList) {
            val ansMap = objectMapper.readerFor(object: TypeReference<Map<Int, String>>(){})
                .readValue<Map<Int, String>>(answer.answers)
            answers[answer.communityId!!] = ansMap
        }
        val userAnswer = objectMapper.readerFor(object: TypeReference<Map<Int, String>>(){})
            .readValue<Map<Int, String>>(
                answerRepository.findByUserId(user.id)
                    .orElseThrow { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not answered the questions") }
                    .answers
            )
        val userInteractions = tinderInteractionRepository.findByUserId(user.id).associateBy { it.communityId }

        val communityProfiles = communities.map { CommunitiesTinderDto(
            id = it.id,
            name = it.name,
            matchedAnswers = answers[it.id]!!.entries.count { ans ->
                userAnswer[ans.key] == ans.value
            },
            status = userInteractions[it.id]?.let { interaction ->
                when (interaction.liked) {
                    true -> TinderStatus.LIKED
                    false -> TinderStatus.DISLIKED
                }
            } ?: TinderStatus.NOT_SEEN,
            shortDescription = it.shortDescription,
            descriptionParagraphs = it.descriptionParagraphs,
            website = it.website,
            logo = it.logo,
            established = it.established,
            email = it.email,
            interests = it.interests.split(","),
            facebook = it.facebook,
            instagram = it.instagram,
            application = it.application,
            resortName = it.resortName,
            tinderAnswers = answers[it.id]!!.values.toList()
        ) }
        return communityProfiles
    }

}