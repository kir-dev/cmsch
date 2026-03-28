package hu.bme.sch.cmsch.component.pushnotification

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.retry.RetryPolicy
import org.springframework.core.retry.RetryTemplate
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration
@ConditionalOnBean(PushNotificationComponent::class)
class FirebaseMessagingConfiguration {
    private val messagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun googleCredentials(
        @Value("\${hu.bme.sch.cmsch.google.service-account-key}") accountKey: String,
    ): GoogleCredentials = GoogleCredentials
        .fromStream(accountKey.byteInputStream())
        .createScoped(listOf(messagingScope))

    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun fcmProjectId(credentials: GoogleCredentials): String =
        (credentials as? ServiceAccountCredentials)?.projectId ?: "unknown"

    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun fcmWebClient(builder: WebClient.Builder): WebClient = builder.baseUrl("https://fcm.googleapis.com/v1/projects/").build()

    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun retryTemplate(): RetryTemplate {
        val retryPolicy = RetryPolicy.builder()
            .maxRetries(5)
            .delay(Duration.ofMillis(500))
            .multiplier(1.5)
            .build()

        val template = RetryTemplate()
        template.setRetryPolicy(retryPolicy)
        return template
    }
}
