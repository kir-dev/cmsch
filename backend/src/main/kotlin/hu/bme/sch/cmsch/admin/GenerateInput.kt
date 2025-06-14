package hu.bme.sch.cmsch.admin

import hu.bme.sch.cmsch.model.RoleType


enum class InputType(val value: String) {
    TEXT("text"),
    NUMBER("number"),
    DATE("date"),
    SQL_DATE("sql-date"),
    TIME("time"),
    FLOAT("float"),
    FLOAT3("float3"),
    BLOCK_TEXT("textarea"),
    COLOR("color"),
    BLOCK_TEXT_ANSWER("textarea-answer"),
    BLOCK_TEXT_MARKDOWN("markdown"),
    FILE("file"),
    SWITCH("checkbox"),
    HIDDEN("hidden"),
    BLOCK_SELECT("select"),
    PERMISSIONS("permissions"),
    PERMISSION_GROUPS("permission-groups"),
    ENTITY_SELECT("entity-select"),
    LIST_ENTITIES("list-entities"),
    IMAGE_PREVIEW("image-preview"),
    FILE_PREVIEW("file-preview"),
    FORM_EDITOR("form-editor"),
    BOOLEAN_LIST("boolean-list"),
    TASK_SUBMISSION_HISTORY("task-submission-history"),
    SECTION_SEPARATOR("section-separator"),
    DOCS("docs"),
    TOKEN_QR_TEXT_FIELD("token-qr-text-field");
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
    val type: InputType = InputType.TEXT,
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
