package hu.bme.sch.cmsch.admin

import hu.bme.sch.cmsch.model.RoleType


object InputType {
    const val TEXT = "text"
    const val NUMBER = "number"
    const val DATE = "date"
    const val SQL_DATE = "sql-date"
    const val TIME = "time"
    const val FLOAT = "float"
    const val FLOAT3 = "float3"
    const val BLOCK_TEXT = "textarea"
    const val COLOR = "color"
    const val BLOCK_TEXT_ANSWER = "textarea-answer"
    const val BLOCK_TEXT_MARKDOWN = "markdown"
    const val FILE = "file"
    const val SWITCH = "checkbox"
    const val HIDDEN = "hidden"
    const val BLOCK_SELECT = "select"
    const val PERMISSIONS = "permissions"
    const val PERMISSION_GROUPS = "permission-groups"
    const val ENTITY_SELECT = "entity-select"
    const val LIST_ENTITIES = "list-entities"
    const val IMAGE_PREVIEW = "image-preview"
    const val FILE_PREVIEW = "file-preview"
    const val FORM_EDITOR = "form-editor"
    const val BOOLEAN_LIST = "boolean-list"
    const val TASK_SUBMISSION_HISTORY = "task-submission-history"
    const val SECTION_SEPARATOR = "section-separator"
    const val DOCS = "docs"
    const val TOKEN_QR_TEXT_FIELD = "token-qr-text-field"
}

enum class InputInterpreter(val value: String) {
    INHERIT("inherit"),
    PATH("path"),
    SEARCH("search"),
    CUSTOM("custom"),
}

const val STYLE_SECURE = "secure"

const val ICON_SECURE = "encrypted"

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateInput(
    val type: String = InputType.TEXT,
    val min: Int = 0,
    val max: Int = Integer.MAX_VALUE,
    val maxLength: Int = 255,
    val label: String = "",
    val note: String = "",
    val placeholder: String = "",
    val autocomplete: String = "off",
    val defaultValue: String = "",
    val order: Int = 0,
    val visible: Boolean = true,
    val enabled: Boolean = true,
    val ignore: Boolean = false,
    val interpreter: InputInterpreter = InputInterpreter.INHERIT,
    val fileId: String = "0",
    val fileType: String = "image",
    val source: Array<String> = [],
    val entitySource: String = "Nothing",
    val minimumRole: RoleType = RoleType.STAFF,
    val icon: String = "",
    val style: String = "",
)
