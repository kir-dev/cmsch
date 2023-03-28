package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.task.TaskStatus

data class FilledOutFormDto(
    var internalId: String = "",
    var email: String = "",
    var name: String = "",
    var neptun: String? = null,
    var submittedAt: Long = 0,
    var accepted: Boolean = false,
    var rejected: Boolean = false,
    var lastUpdatedAt: Long = 0,
    var formSubmission: Map<String, String> = mapOf(),
    var profilePictureUrl: String = "",
    val profileStatus: TaskStatus,
    var cvUrl: String = "",
    val cvStatus: TaskStatus,
    val detailsValidated: Boolean,
)
