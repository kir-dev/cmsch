package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
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
    VOTE, // value = [{"title":"","value":"","img":"","text":""}, {...}]

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
    },
    INJECT_UNIT_SCOPE(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.unitScopes ?: ""
        }
    },
    INJECT_PROFILE_PICTURE(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.profilePicture
        }
    };

    open fun fetchValue(user: UserEntity): String {
        return "not-server-side-value"
    }
}

data class SignupFormElement(
    @JsonView(FullDetails::class)
    var fieldName: String = "",

    @JsonView(FullDetails::class)
    var label: String = "",

    @JsonView(FullDetails::class)
    var type: FormElementType = FormElementType.TEXT,

    @JsonView(FullDetails::class)
    var formatRegex: String = "",

    @JsonView(FullDetails::class)
    var invalidFormatMessage: String = "",

    @JsonView(FullDetails::class)
    var values: String = "",

    @JsonView(FullDetails::class)
    var note: String = "",

    @JsonView(FullDetails::class)
    var required: Boolean = false,

    @JsonView(FullDetails::class)
    var permanent: Boolean = false, // Cannot be edited after submission
)
