package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

enum class FormStatus {
    NO_SUBMISSION,
    SUBMITTED,
    REJECTED,
    ACCEPTED,

    TOO_EARLY,
    TOO_LATE,
    NOT_ENABLED,
    NOT_FOUND,
    FULL,
    GROUP_NOT_PERMITTED
}

enum class FormSubmissionStatus {
    OK,
    OK_RELOG_REQUIRED,
    ALREADY_SUBMITTED,
    INVALID_VALUES,
    FORM_NOT_AVAILABLE,
    FORM_IS_FULL,
    EDIT_SUBMISSION_NOT_FOUND,
    EDIT_CANNOT_BE_CHANGED
}

data class FormView(

    @field:JsonView(FullDetails::class)
    val form: FormEntityDto? = null,

    @field:JsonView(FullDetails::class)
    val submission: Map<String, String>? = null,

    @field:JsonView(FullDetails::class)
    val detailsValidated: Boolean? = null,

    @field:JsonView(FullDetails::class)
    val status: FormStatus = FormStatus.NO_SUBMISSION,

    /**
     * Markdown message
     * - status = REJECTED -> rejection status
     * - status = SUBMITTED -> how to pay
     * - status = ACCEPTED -> what to do
     * - else -> just print to the top of the page
     */
    @field:JsonView(FullDetails::class)
    val message: String? = null

)
