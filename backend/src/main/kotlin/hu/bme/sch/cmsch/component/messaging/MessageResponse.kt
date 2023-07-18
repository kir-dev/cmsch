package hu.bme.sch.cmsch.component.messaging

data class MessageResponse(
    val succeed: Boolean,
    val message: String?,
    val delivered: List<String>
)