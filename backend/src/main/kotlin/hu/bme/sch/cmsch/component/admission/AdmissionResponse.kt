package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.model.RoleType

enum class EntryRole(
    val value: Int,
    val canAttend: Boolean
) {
    BANNED(0, false),
    CANNOT_ATTEND(1, false),
    USER(10, true),
    VIP(20, true),
    ORGANIZER(30, true),
    PERFORMER(40, true),
    LEAD_ORGANIZER(100, true)
}

data class AdmissionResponse(
    val groupName: String,
    val userName: String,
    val role: RoleType,
    val entryRole: EntryRole,
    val accessGranted: Boolean,
    val comment: String = ""
)
