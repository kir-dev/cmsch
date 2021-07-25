package hu.bme.sch.g7.admin

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class GenerateOverview(
    val visible: Boolean = true,
    val columnName: String = "",
    val centered: Boolean = false,
    val order: Int = 0
)
