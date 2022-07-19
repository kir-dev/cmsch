package hu.bme.sch.cmsch.component.signup

import hu.bme.sch.cmsch.model.UserEntity

enum class FormElementType(
    val serverSide: Boolean = false,
    val persist: Boolean = true
) {

    TEXT,
    LONG_TEXT,
    NUMBER,
    EMAIL,
    PHONE,
    CHECKBOX,
    SELECT,
    MUST_AGREE,

    INFO_BOX(persist = false),
    WARNING_BOX(persist = false),
    TEXT_BOX(persist = false),
    SECTION_START(persist = false),

    INJECT_USER_FULLNAME(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.fullName
        }
    },
    INJECT_USER_NEPTUN(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.neptun
        }
    },
    INJECT_USER_EMAIL(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.email
        }
    },
    INJECT_USER_INTERNAL_ID(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.internalId
        }
    },
    INJECT_USER_CMSCH_ID(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.cmschId
        }
    },
    INJECT_GROUP_NAME(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.groupName
        }
    };

    open fun fetchValue(user: UserEntity): String {
        return "not-server-side-value"
    }
}

data class SignupFormElement(
    var fieldName: String,
    var label: String,
    var type: FormElementType,
    var formatRegex: String,
    var invalidFormatMessage: String,
    var values: String,
    var note: String,
    var required: Boolean,
    var permanent: Boolean, // Cannot be edited after submission
)
