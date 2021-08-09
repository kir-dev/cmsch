package hu.bme.sch.g7.admin

const val OVERVIEW_TYPE_TEXT = "text"
const val OVERVIEW_TYPE_DATE = "date"
const val OVERVIEW_TYPE_BOOLEAN = "boolean"

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateOverview(
    val visible: Boolean = true,
    val columnName: String = "",
    val centered: Boolean = false,
    val order: Int = 0,
    val renderer: String = OVERVIEW_TYPE_TEXT
)
