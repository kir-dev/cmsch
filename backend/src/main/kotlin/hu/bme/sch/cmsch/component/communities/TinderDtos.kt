package hu.bme.sch.cmsch.component.communities

data class TinderQuestionDto(
    var id: Int = 0,
    var question: String = "",
    var options: List<String> = emptyList()
) {
    constructor(entity: TinderQuestionEntity) : this(
        id = entity.id,
        question = entity.question,
        options = entity.answerOptions.split(",").map { it.trim() }
    )
}

class TinderAnswerStatus(
    var answered: Boolean = false,
    var answer: Map<String, String> = emptyMap()
)

enum class TinderAnswerResponseStatus {
    OK,
    INVALID_ANSWER,
    NO_PERMISSION,
    ERROR
}

enum class TinderStatus{
    NOT_SEEN,
    LIKED,
    DISLIKED
}

data class TinderInteractionDto(
    var communityId: Int = 0,
    var liked: Boolean = true
)

data class CommunitiesTinderDto(
    var id: Int = 0,
    var name: String = "",
    var matchedAnswers: Int = 0,
    var status: TinderStatus = TinderStatus.NOT_SEEN,
    var shortDescription: String = "",
    var descriptionParagraphs: String = "",
    var website: String = "",
    var logo: String = "",
    var established: String = "",
    var email: String = "",
    var interests: List<String> = listOf(),
    var facebook: String = "",
    var instagram: String = "",
    var application: String = "",
    var resortName: String = "",
    var tinderAnswers: List<String> = listOf()
)