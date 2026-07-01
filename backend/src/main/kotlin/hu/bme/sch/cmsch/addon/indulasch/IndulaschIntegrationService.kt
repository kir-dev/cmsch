package hu.bme.sch.cmsch.addon.indulasch

import tools.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.qrfight.QrFightComponent
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
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
            for (v in entries)
                if (v.name == name)
                    return v
            return null
        }
    }
}


data class IndulaschTextWidgetDto(
    var title: String?,
    var subtitle: String?
){
    val name = "text"
}

@Service
@ConditionalOnBean(QrFightComponent::class)
class IndulaschIntegrationService(
    private val qrFightComponent: QrFightComponent,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    var indulaschApi = WebClient.builder()
        .baseUrl("https://api.indulasch.kir-dev.hu")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.USER_AGENT, "AuthSchKotlinAPI")
        .build()

    @Async
    fun setTextOnWidget(widgetData: IndulaschTextWidgetDto){
        if(qrFightComponent.indulaschApiKey.isEmpty() || qrFightComponent.indulaschKioskId.isEmpty()) return
        val response: String? = indulaschApi.patch()
            .uri { uriBuilder ->
                uriBuilder.path("/admin/kiosk/${qrFightComponent.indulaschKioskId}/widget")
                    .build()
            }
            .header("Authorization", "Api-Key ${qrFightComponent.indulaschApiKey}")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(widgetData))
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        log.info("IndulaSch text widget set to {}, response: {}", widgetData, response)
    }
}
