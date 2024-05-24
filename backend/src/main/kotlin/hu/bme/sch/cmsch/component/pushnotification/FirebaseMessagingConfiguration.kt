package hu.bme.sch.cmsch.component.pushnotification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.BackOffPolicyBuilder
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
class FirebaseMessagingConfiguration {
    private val messagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun createFirebaseApp(
        @Value("\${hu.bme.sch.cmsch.google.service-account-key}") accountKey: String,
    ): FirebaseApp =
        runCatching { FirebaseApp.getInstance() }
            .getOrElse { initFirebaseApp(accountKey) } // Has to be done this way, to prevent crashing when reloading the application in development


    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun createFirebaseMessaging(app: FirebaseApp): FirebaseMessaging = FirebaseMessaging.getInstance(app)


    private fun initFirebaseApp(accountKey: String): FirebaseApp {
        val credentials = GoogleCredentials
            .fromStream(accountKey.byteInputStream())
            .createScoped(listOf(messagingScope))

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()

        return FirebaseApp.initializeApp(options)
    }


    @Bean
    @ConditionalOnBean(PushNotificationComponent::class)
    fun retryTemplate(): RetryTemplate {
        val retryPolicy = SimpleRetryPolicy(5)
        val backOffPolicy = BackOffPolicyBuilder.newBuilder()
            .delay(500L)
            .multiplier(1.5)
            .build()

        val template = RetryTemplate()
        template.setRetryPolicy(retryPolicy)
        template.setBackOffPolicy(backOffPolicy)
        return template
    }
}
