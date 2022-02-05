package hu.bme.sch.cmsch.dto

enum class GroupSelectionResponseType { OK, INVALID_GROUP, LEAVE_DENIED, UNAUTHORIZED }

data class GroupSelectionResponse(val status: GroupSelectionResponseType)
