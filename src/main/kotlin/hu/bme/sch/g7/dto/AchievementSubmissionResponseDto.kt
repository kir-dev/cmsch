package hu.bme.sch.g7.dto

enum class AchievementSubmissionStatus {
    OK,
    EMPTY_ANSWER,
    INVALID_IMAGE,
    ALREADY_SUBMITTED,
    ALREADY_APPROVED,
    NO_ASSOCIATE_GROUP,
    INVALID_ACHIEVEMENT_ID,
    TOO_EARLY_OR_LATE
}

class AchievementSubmissionResponseDto(
        var status: AchievementSubmissionStatus
)