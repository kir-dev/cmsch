package hu.bme.sch.cmsch.component.signup

enum class FormElementType(
    val serverSide: Boolean = false
) {
    TEXT,
    LONG_TEXT,
    NUMBER,
    EMAIL,
    PHONE,
    CHECKBOX,
    SELECT,
    MUST_AGREE,

    INFO_BOX,
    WARNING_BOX,
    TEXT_BOX,
    SECTION_START,

    INJECT_USER_FULLNAME(serverSide = true),
    INJECT_USER_NEPTUN(serverSide = true),
    INJECT_USER_EMAIL(serverSide = true),
    INJECT_USER_INTERNAL_ID(serverSide = true),
    INJECT_USER_CMSCH_ID(serverSide = true),
    INJECT_GROUP_NAME(serverSide = true),
}

data class SignupFormElement(
    var label: String,
    var type: FormElementType,
    var formatRegex: String,
    var invalidFormatMessage: String,
    var values: String,
    var note: String,
    var required: Boolean,
    var permanent: Boolean, // Cannot be edited after submission
)
