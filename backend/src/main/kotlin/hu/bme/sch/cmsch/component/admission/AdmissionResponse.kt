package hu.bme.sch.cmsch.component.admission

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity

enum class EntryRole(
    val value: Int,
    val canAttend: Boolean
) {
    BANNED(0, false),
    CANNOT_ATTEND(1, false),
    USER(10, true),
    ORGANIZER(20, true),
    VIP(30, true),
    PERFORMER(40, true),
    LEAD_ORGANIZER(100, true)
}

data class AdmissionResponse(
    var groupName: String,
    val userName: String,
    val role: RoleType,
    val entryRole: EntryRole,
    val accessGranted: Boolean,
    val comment: String = "",

    @field:JsonIgnore
    val userEntity: UserEntity? = null,
    @field:JsonIgnore
    val formId: Int? = null,
    @field:JsonIgnore
    val responseId: Int? = null,
)
