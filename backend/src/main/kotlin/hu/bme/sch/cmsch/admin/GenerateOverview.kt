package hu.bme.sch.cmsch.admin

/**
 * For sorters: https://tabulator.info/docs/5.4/sort#func-builtin
 */
enum class OverviewType(val value: String, val sorter: String, val formatSettings: Array<Pair<String, Any>>) {
    ID("id", "number",
        arrayOf("width" to 100, "vertAlign" to "middle", "visible" to false)),
    TEXT("text", "string",
        arrayOf("vertAlign" to "middle")),
    DATE("date", "datetime",
        arrayOf("vertAlign" to "middle", "formatter" to "datetime")),
    COLOR("color", "string",
        arrayOf("formatter" to "color", "vertAlign" to "middle")),
    BOOLEAN("boolean", "boolean",
        arrayOf("formatter" to "tickCross", "width" to 120)),
    ICON("icon", "string",
        arrayOf("vertAlign" to "middle", "formatter" to "enumIconsFormatter", "width" to 120)),
    TIME("time", "time",
        arrayOf("vertAlign" to "middle")),
    NUMBER("number", "number",
        arrayOf("vertAlign" to "middle")),
    IMAGE("image", "string",
        arrayOf("formatter" to "image", "width" to 120, "formatterParams" to mapOf("height" to "100px")));
}


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateOverview(
    val visible: Boolean = true,
    val columnName: String = "",
    val centered: Boolean = false,
    val order: Int = 0,
    val renderer: OverviewType = OverviewType.TEXT,
    val useForSearch: Boolean = true
)

fun GenerateOverview.alignment(): String {
    return if (this.centered) "center" else "left"
}

fun GenerateOverview.formatValue(value: Any?): Any =
    if (renderer == OverviewType.IMAGE) {
        if (value is String && value.isNotBlank())
            value
        else
            "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" // empty image
    } else if (renderer == OverviewType.TEXT || renderer == OverviewType.ICON ) {
        value ?: ""
    } else {
        value ?: 0
    }
