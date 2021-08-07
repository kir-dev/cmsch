package hu.bme.sch.g7.admin

const val INPUT_TYPE_TEXT = "text"
const val INPUT_TYPE_NUMBER = "number"
const val INPUT_TYPE_BLOCK_TEXT = "textarea"
const val INPUT_TYPE_FILE = "file"
const val INPUT_TYPE_SWITCH = "checkbox"
const val INPUT_TYPE_HIDDEN = "hidden"
const val INPUT_TYPE_BLOCK_SELECT = "select"
const val INPUT_TYPE_ENTITY_SELECT = "entity-select"
const val INPUT_TYPE_LIST_ENTITIES = "list-entities"
const val INPUT_TYPE_IMAGE_PREVIEW = "image-preview"

const val INTERPRETER_INHERIT = "inherit"

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateInput(
        val type: String = INPUT_TYPE_TEXT,
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
        val interpreter: String = INTERPRETER_INHERIT,
        val fileId: String = "0",
        val fileType: String = "image",
        val source: Array<String> = [],
        val entitySource: String = "Nothing"
)
