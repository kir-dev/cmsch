package hu.bme.sch.cmsch.component.messaging

data class SimpleMessageDto(
    val token: String,
    val target: List<String>,
    var message: String,
)