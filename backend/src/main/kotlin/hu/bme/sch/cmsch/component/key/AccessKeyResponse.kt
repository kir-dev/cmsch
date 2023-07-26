package hu.bme.sch.cmsch.component.key

data class AccessKeyResponse(
    val success: Boolean,
    val reason: String,
    val refreshSession: Boolean,
)
