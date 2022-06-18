package hu.bme.sch.cmsch.component.groupselection

enum class GroupSelectionResponseType { OK, INVALID_GROUP, LEAVE_DENIED, PERMISSION_DENIED, UNAUTHORIZED }

data class GroupSelectionResponse(val status: GroupSelectionResponseType)
