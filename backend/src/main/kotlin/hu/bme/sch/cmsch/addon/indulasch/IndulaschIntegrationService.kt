package hu.bme.sch.cmsch.addon.indulasch

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

enum class IndulaschMessageType {
    INFO,
    WARNING,
    SUCCESS,
    FUN;

    companion object {
        fun getByNameOrNull(name: String): IndulaschMessageType? {
            for (v in values())
                if (v.name == name)
                    return v
            return null
        }
    }
}

data class IndulaschMessageDto(
    var id: Int = 0,
    var type: IndulaschMessageType,
    var text: String = ""
)

data class IndulaschNewMessageDto(
    var type: IndulaschMessageType,
    var text: String = ""
)

@Service
@ConditionalOnBean(QrFightComponent::class)
class IndulaschIntegrationService(
    private val qrFightComponent: QrFightComponent,
    @Value("\${hu.bme.sch.cmsch.indulasch.token:none}") private val indulaschApiToken: String,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    var indulaschApi = WebClient.builder()
        .baseUrl("https://indula.sch.bme.hu/api")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
        .build()

    fun fetchIndulasch(): List<IndulaschMessageDto> {
        val response = indulaschApi.get()
            .uri { uriBuilder ->
                uriBuilder.path("/messages")
                    .build()
            }
            .header("Authorization", "Bearer $indulaschApiToken")
            .retrieve()
            .bodyToFlux(IndulaschMessageDto::class.java)
            .collectList()
            .block()
        log.info("Indulasch api fetched: $response")
        return response ?: listOf()
    }

    fun insertMessage(message: IndulaschNewMessageDto) {
        val response: String? = indulaschApi.put()
            .uri { uriBuilder ->
                uriBuilder.path("/messages")
                    .build()
            }
            .header("Authorization", "Bearer $indulaschApiToken")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(message))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        log.info("Indulasch message {} inserted, response: {}", message, response)
    }

    fun deleteMessage(id: Int) {
        val response: String? = indulaschApi.delete()
            .uri { uriBuilder ->
                uriBuilder.path("/messages/${id}")
                    .build()
            }
            .header("Authorization", "Bearer $indulaschApiToken")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        log.info("Indulasch message {} deleted, response: {}", id, response)
    }

}
