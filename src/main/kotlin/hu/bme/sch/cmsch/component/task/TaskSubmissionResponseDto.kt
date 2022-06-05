package hu.bme.sch.cmsch.component.task

enum class TaskSubmissionStatus {
    OK,
    EMPTY_ANSWER,
    INVALID_IMAGE,
    INVALID_PDF,
    ALREADY_SUBMITTED,
    ALREADY_APPROVED,
    NO_ASSOCIATE_GROUP,
    INVALID_TASK_ID,
    TOO_EARLY_OR_LATE,
    NO_PERMISSION,
    INVALID_BACKEND_CONFIG
}

class TaskSubmissionResponseDto(
    var status: TaskSubmissionStatus
)
