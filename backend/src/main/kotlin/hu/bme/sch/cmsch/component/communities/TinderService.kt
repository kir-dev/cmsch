package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.AuditLogService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import kotlin.collections.forEach
import kotlin.collections.set
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull


@Service
@ConditionalOnBean(CommunitiesComponent::class)
class TinderService(
    private val communityRepository: CommunityRepository,
    private val questionRepository: TinderQuestionRepository,
    private val answerRepository: TinderAnswerRepository,
    private val objectMapper: ObjectMapper,
    private val auditLog: AuditLogService,
    private val tinderInteractionRepository: TinderInteractionRepository
) {

    private val reader = objectMapper.readerFor(object: TypeReference<Map<String, String>>(){})

    @Transactional(readOnly = true)
    fun getAllQuestions() = questionRepository.findAll().toList()

    @Transactional(readOnly = true)
    fun getAnswerForCommunity(communityId: Int) = answerRepository.findByCommunityId(communityId)

    @Transactional(readOnly = true)
    fun getAnswerMapForUser(userId: Int) = answerRepository.findByUserId(userId)
        .map { reader.readValue<Map<String, String>>( it.answers ) }

    @Transactional
    fun submitAnswers(update: Boolean, user: CmschUser, answers: Map<String, String>): TinderAnswerResponseStatus {
        val questions = questionRepository.findAll().associateBy { it.question }
        for (answer in answers) {
            val question = questions[answer.key] ?: continue
            if (answer.value!="" && !question.answerOptions.split(",").map { it.trim() }.contains(answer.value)) {
                return TinderAnswerResponseStatus.INVALID_ANSWER
            }
        }

        val existing = answerRepository.findByUserId(user.id)
        if (!update) {
            if (existing.isPresent){
                return TinderAnswerResponseStatus.ERROR
            }
            val entity = TinderAnswerEntity(
                userId = user.id,
                userName = user.userName,
                answers = objectMapper.writeValueAsString(answers)
            )
            answerRepository.save(entity)
        } else {
            val answer = mutableMapOf<String, String>()
            existing.ifPresent {
                reader.readValue<Map<String, String>>(it.answers).forEach { (k, v) ->
                    answer[k] = v
                }
            }
            for (ans in answers) {
                answer[ans.key] = ans.value
            }
            existing.ifPresentOrElse(
                {
                    it.answers = objectMapper.writeValueAsString(answer)
                    answerRepository.save(it)
                },
                {
                    val entity = TinderAnswerEntity(
                        userId = user.id,
                        userName = user.userName,
                        answers = objectMapper.writeValueAsString(answer)
                    )
                    answerRepository.save(entity)
                }
            )
        }
        return TinderAnswerResponseStatus.OK
    }

    @Transactional(readOnly = true)
    fun getTinderCommunities(user: UserEntity): List<CommunitiesTinderDto> {
        val communities = communityRepository.findAll().toList()
        val answerList = answerRepository.findAllWithCommunityIdNotNull()
        val answers = mutableMapOf<Int, Map<String, String>>()
        for (answer in answerList) {
            val ansMap = reader.readValue<Map<String, String>>(answer.answers)
            answers[answer.communityId!!] = ansMap
        }
        val userAnswer = reader.readValue<Map<String, String>>(
            answerRepository.findByUserId(user.id)
                .getOrElse { return emptyList() }
                .answers
        )
        val userInteractions = tinderInteractionRepository.findByUserId(user.id).associateBy { it.communityId }

        val communityProfiles = communities.map { CommunitiesTinderDto(
            id = it.id,
            name = it.name,
            matchedAnswers = answers[it.id]?.entries?.count { ans ->
                userAnswer[ans.key] == ans.value
            } ?: 0,
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
            tinderAnswers = answers[it.id]?.values?.toList() ?: emptyList()
        ) }
        return communityProfiles
    }

    @Transactional
    fun ensureCommunityAnswer(community: CommunityEntity): TinderAnswerEntity {
        val existing = answerRepository.findByCommunityId(community.id)
        return existing.getOrElse {
            answerRepository.save(TinderAnswerEntity(
                userId = null,
                communityId = community.id,
                communityName = community.name,
                answers = "{}"
            ))
        }
    }

    @Transactional
    fun updateCommunityAnswer(user: CmschUser, communityId: Int, data: Map<String, String>): String {
        val questions = getAllQuestions()
        val community = communityRepository.findById(communityId).getOrNull()
            ?: return "redirect:/admin/control/community"
        val prevAnswer = ensureCommunityAnswer(community)

        val answer = mutableMapOf<String, String>()
        reader.readValue<Map<String, String>>(prevAnswer.answers)
            .entries.forEach {
                answer[it.key] = it.value
            }
        for (question in questions) {
            val ans = data[question.question] ?: continue
            val options = question.answerOptions.split(',').map { it.trim() }
            if (options.contains(ans)) {
                answer[question.question] = ans
            } else if (ans != "") {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Hibás válaszlehetőség: $ans")
            }
        }
        prevAnswer.answers = objectMapper.writeValueAsString(answer)

        auditLog.edit(user, "communities", "$prevAnswer answers: $answer")

        answerRepository.save(prevAnswer)

        return "redirect:/admin/control/community"
    }

    @Transactional
    fun interactWithCommunity(user: UserEntity, interaction: TinderInteractionDto): Boolean {
        val community = communityRepository.findById(interaction.communityId).getOrNull() ?: return false
        if (tinderInteractionRepository.findByCommunityIdAndUserId(community.id, user.id).isPresent){
            return false
        }
        val entity = TinderInteractionEntity(
            communityId = community.id,
            userId = user.id,
            liked = interaction.liked
        )
        tinderInteractionRepository.save(entity)
        return true
    }

}
