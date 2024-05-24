package hu.bme.sch.cmsch.dto

import com.google.firebase.messaging.*

data class CmschNotification(
    var title: String,
    var body: String,
    var image: String?,
    var link: String?,
) {
    private fun createNotification(): Notification {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
        if (image != null) notification.setImage(image)
        return notification.build()
    }

    fun addToMessage(builder: Message.Builder) {
        builder.setNotification(createNotification())

        if (link != null) {
            val webpushConfig = WebpushConfig.builder().setFcmOptions(WebpushFcmOptions.withLink(link)).build()
            builder.setWebpushConfig(webpushConfig)
        }
    }

    fun addToMessage(builder: MulticastMessage.Builder) {
        builder.setNotification(createNotification())

        if (link != null) {
            val webpushConfig = WebpushConfig.builder().setFcmOptions(WebpushFcmOptions.withLink(link)).build()
            builder.setWebpushConfig(webpushConfig)
        }
    }
}
