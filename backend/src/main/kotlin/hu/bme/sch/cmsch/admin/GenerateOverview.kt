package hu.bme.sch.cmsch.admin

const val OVERVIEW_TYPE_ID = "id"
const val OVERVIEW_TYPE_TEXT = "text"
const val OVERVIEW_TYPE_DATE = "date"
const val OVERVIEW_TYPE_BOOLEAN = "boolean"
const val OVERVIEW_TYPE_ICON = "icon"
const val OVERVIEW_TYPE_TIME = "time"
const val OVERVIEW_TYPE_NUMBER = "number"
const val OVERVIEW_TYPE_CDN_IMAGE = "cdn-image"

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateOverview(
    val visible: Boolean = true,
    val columnName: String = "",
    val centered: Boolean = false,
    val order: Int = 0,
    val renderer: String = OVERVIEW_TYPE_TEXT,
    val cdnImageFolder: String = "",
    val useForSearch: Boolean = true
)

fun GenerateOverview.alignment(): String {
    return if (this.centered) "center" else "left"
}

/**
 * https://tabulator.info/docs/5.4/sort#func-builtin
 */
fun GenerateOverview.sorter(): String {
    return when (this.renderer) {
        OVERVIEW_TYPE_ID -> "number"
        OVERVIEW_TYPE_TEXT -> "string"
        OVERVIEW_TYPE_DATE -> "datetime"
        OVERVIEW_TYPE_BOOLEAN -> "boolean"
        OVERVIEW_TYPE_TIME -> "time"
        OVERVIEW_TYPE_NUMBER -> "number"
        OVERVIEW_TYPE_ICON -> "string"
        else -> "string"
    }
}

fun GenerateOverview.formatValue(value: Any?): Any =
    if (renderer == OVERVIEW_TYPE_CDN_IMAGE) {
        if (value is String && value.isNotBlank())
            "/cdn/${cdnImageFolder}/${value}"
        else
            "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" // empty image
    } else if (renderer == OVERVIEW_TYPE_TEXT || renderer == OVERVIEW_TYPE_ICON) {
        value ?: ""
    } else {
        value ?: 0
    }

fun GenerateOverview.extra(): Array<Pair<String, Any>> {
    return when (this.renderer) {
        OVERVIEW_TYPE_ID -> arrayOf("width" to 100, "vertAlign" to "middle", "visible" to false)
        OVERVIEW_TYPE_TEXT -> arrayOf("vertAlign" to "middle")
        OVERVIEW_TYPE_DATE -> arrayOf("vertAlign" to "middle", "formatter" to "datetime")
        OVERVIEW_TYPE_BOOLEAN -> arrayOf("formatter" to "tickCross", "width" to 120)
        OVERVIEW_TYPE_CDN_IMAGE -> arrayOf(
            "formatter" to "image",
            "width" to 120,
            "formatterParams" to mapOf("height" to "100px")
        )

        OVERVIEW_TYPE_TIME -> arrayOf("vertAlign" to "middle")
        OVERVIEW_TYPE_NUMBER -> arrayOf("vertAlign" to "middle")
        OVERVIEW_TYPE_ICON -> arrayOf("vertAlign" to "middle", "formatter" to "enumIconsFormatter", "width" to 120)
        else -> emptyArray()
    }
}
