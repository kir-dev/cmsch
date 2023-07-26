package hu.bme.sch.cmsch.component.key

data class AccessKeyView(
    val title: String,
    val topMessage: String,
    val fieldName: String,
    val enabled: Boolean,
)