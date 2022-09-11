package hu.bme.sch.cmsch.component.team

enum class TeamCreationStatus {
    INVALID_NAME,
    USED_NAME,
    ALREADY_IN_GROUP,
    CREATION_DISABLED,
    INSUFFICIENT_PERMISSIONS,
    OK,
    INTERNAL_ERROR
}
