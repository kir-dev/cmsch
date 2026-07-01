package hu.bme.sch.cmsch.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CmschNotification(
    var title: String,
    var body: String,
    var image: String?,
    var link: String?,
) {
    fun toFcmRequest(token: String): FcmRequest {
        return FcmRequest(
            message = FcmMessage(
                token = token,
                notification = FcmNotification(
                    title = title,
                    body = body,
                    image = image
                ),
                webpush = link?.let { FcmWebpush(fcmOptions = FcmWebpushOptions(link = it)) }
            )
        )
    }
}

data class FcmRequest(
    val message: FcmMessage
)

data class FcmMessage(
    val token: String,
    val notification: FcmNotification,
    val webpush: FcmWebpush? = null
)

data class FcmNotification(
    val title: String,
    val body: String,
    val image: String? = null
)

data class FcmWebpush(
    @get:JsonProperty("fcm_options")
    val fcmOptions: FcmWebpushOptions
)

data class FcmWebpushOptions(
    val link: String
)
