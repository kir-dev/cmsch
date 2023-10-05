package hu.bme.sch.cmsch.admin

const val OVERVIEW_TYPE_ID = "id"
const val OVERVIEW_TYPE_TEXT = "text"
const val OVERVIEW_TYPE_DATE = "date"
const val OVERVIEW_TYPE_BOOLEAN = "boolean"
const val OVERVIEW_TYPE_ICON = "icon"
const val OVERVIEW_TYPE_TIME = "time"
const val OVERVIEW_TYPE_NUMBER = "number"

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateOverview(
    val visible: Boolean = true,
    val columnName: String = "",
    val centered: Boolean = false,
    val order: Int = 0,
    val renderer: String = OVERVIEW_TYPE_TEXT,
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

fun GenerateOverview.extra(): String {
    return when (this.renderer) {
        OVERVIEW_TYPE_ID -> ", \"width\":100, \"vertAlign\":\"middle\", \"visible\":false"
        OVERVIEW_TYPE_TEXT -> ", \"vertAlign\":\"middle\""
        OVERVIEW_TYPE_DATE -> ", \"vertAlign\":\"middle\",\"formatter\":\"datetime\""
        OVERVIEW_TYPE_BOOLEAN -> ", \"formatter\":\"tickCross\", \"width\":120"
        OVERVIEW_TYPE_TIME -> ", \"vertAlign\":\"middle\""
        OVERVIEW_TYPE_NUMBER -> ", \"vertAlign\":\"middle\""
        OVERVIEW_TYPE_ICON -> ", \"vertAlign\":\"middle\", \"formatter\":\"enumIconsFormatter\", \"width\":120"
        else -> ""
    }
}
