package hu.bme.sch.cmsch.component.signup

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

data class SignupFormView(

    @JsonView(FullDetails::class)
    val form: SignupFormEntityDto? = null,

    @JsonView(FullDetails::class)
    val submission: SignupResponseEntity? = null,

    @JsonView(FullDetails::class)
    val status: FormStatus = FormStatus.NO_SUBMISSION,

    /**
     * Markdown message
     * - status = REJECTED -> rejection status
     * - status = SUBMITTED -> how to pay
     * - status = ACCEPTED -> what to do
     * - else -> just print to the top of the page
      */
    @JsonView(FullDetails::class)
    val message: String? = null

)
