package hu.bme.sch.cmsch.component.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
@ConditionalOnBean(MessagingComponent::class)
class MessagingService(
    private val messagingComponent: MessagingComponent
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val writer = ObjectMapper().writerFor(SimpleMessageDto::class.java)

    @Async
    open fun sendSimpleMessage(
        target: List<String>,
        message: String,
        proxyBaseUrl: String?,
        proxyToken: String?,
        callback: (MessageResponse) -> Unit = {},
    ) {
        val client = WebClient.builder()
            .baseUrl(proxyBaseUrl ?: messagingComponent.proxyBaseUrl.getValue())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
            .build()

        log.info("Distributing simple message: '{}' to {}", message, target.joinToString(", "))
        val messageResponse = client.post()
            .uri { uriBuilder -> uriBuilder.path("/api/message/simple").build() }
            .body(
                BodyInserters.fromValue(
                    writer.writeValueAsString(
                        SimpleMessageDto(
                            token = proxyToken ?: messagingComponent.proxyToken.getValue(),
                            target = target,
                            message = message
                        )
                    )
                )
            )
            .retrieve()
            .bodyToMono(MessageResponse::class.java)
            .block() ?: MessageResponse(false, "Response is null", listOf())
        log.info("Response was: {} and delivered to: {}", messageResponse.message ?: "<null>", messageResponse.delivered.joinToString(", "))
        callback.invoke(messageResponse)
    }

}