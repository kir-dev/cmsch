package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.dashboard.pascalToKebab
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.UserEntity

enum class FormElementType(
    val serverSide: Boolean = false,
    val persist: Boolean = true,
    val rendersOnServerSide: Boolean = false,
) {

    TEXT(rendersOnServerSide = true),
    LONG_TEXT(rendersOnServerSide = true),
    NUMBER(rendersOnServerSide = true),
    EMAIL,
    PHONE,
    CHECKBOX(rendersOnServerSide = true),
    SELECT(rendersOnServerSide = true),
    MUST_AGREE(rendersOnServerSide = true),
    VOTE, // value = [{"title":"","value":"","img":"","text":""}, {...}]
    SELECTION_GRID, // value = { "options": [{ "key": "lunch", "label": "ebéd" },{"key": "dinner", "label": "vacsora" }], "questions": [{ "key": "1", "label": "T-1" }] }
    CHOICE_GRID, // value = { "options": [{ "key": "lunch", "label": "ebéd" },{"key": "dinner", "label": "vacsora" }], "questions": [{ "key": "1", "label": "T-1" }] }

    INFO_BOX(persist = false, rendersOnServerSide = true),
    WARNING_BOX(persist = false, rendersOnServerSide = true),
    TEXT_BOX(persist = false, rendersOnServerSide = true),
    SECTION_START(persist = false, rendersOnServerSide = true),

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
            return user.unitScopes
        }
    },
    INJECT_PROFILE_PICTURE(serverSide = true) {
        override fun fetchValue(user: UserEntity): String {
            return user.profilePicture
        }
    },

    // BACKEND ONLY
    FILE(rendersOnServerSide = true),
    HISTORY_VIEW(rendersOnServerSide = true),
    LINK_VIEW(rendersOnServerSide = true),
    IMAGE_VIEW(rendersOnServerSide = true),
    HTML_INFO_BOX(rendersOnServerSide = true),
    SEARCHABLE_SELECT(rendersOnServerSide = true),
    CUSTOM_BACKEND_ONLY(rendersOnServerSide = true),
    ;

    val templateName = shoutingSnakeToKebab(name)

    open fun fetchValue(user: UserEntity): String {
        return "not-server-side-value"
    }
}

fun shoutingSnakeToKebab(input: String) = input.split('_').joinToString("-") { it.lowercase() }

@JsonIgnoreProperties(ignoreUnknown = true)
data class FormElement(
    @field:JsonView(FullDetails::class)
    var fieldName: String = "",

    @field:JsonView(FullDetails::class)
    var label: String = "",

    @field:JsonView(FullDetails::class)
    var type: FormElementType = FormElementType.TEXT,

    @field:JsonView(FullDetails::class)
    var formatRegex: String = "",

    @field:JsonView(FullDetails::class)
    var invalidFormatMessage: String = "",

    @field:JsonView(FullDetails::class)
    var values: String = "",

    @field:JsonView(FullDetails::class)
    var note: String = "",

    @field:JsonView(FullDetails::class)
    var required: Boolean = false,

    @field:JsonView(FullDetails::class)
    var permanent: Boolean = false, // Cannot be edited after submission

    @field:JsonView(FullDetails::class)
    var defaultValue: String = "",

    @field:JsonIgnore
    var customType: String? = null,
)