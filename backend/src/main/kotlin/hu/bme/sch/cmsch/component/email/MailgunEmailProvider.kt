package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AuditLogService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException

@Service
@ConditionalOnBean(EmailComponent::class)
class MailgunEmailProvider(
    private val emailComponent: EmailComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val auditLogService: AuditLogService
) : EmailProvider {

    private val log = LoggerFactory.getLogger(javaClass)

    val client = WebClient.builder()
        .baseUrl("https://api.eu.mailgun.net/v3/")
        .defaultHeaders { header -> header.setBasicAuth("api", startupPropertyConfig.mailgunToken) }
        .build()

    override fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("from", "${emailComponent.mailgunAccountName.getValue()} <${emailComponent.mailgunEmailAccount.getValue()}@${emailComponent.mailgunDomain.getValue()}>")
        formData.put("to", to)
        formData.add("subject", subject)
        formData.add("text", content)

        val request = client.method(HttpMethod.POST)
            .uri("${emailComponent.mailgunDomain.getValue()}/messages")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(formData)

        retrieve(request, to, subject, content, responsible)
    }

    override fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("from", "${emailComponent.mailgunAccountName.getValue()} <${emailComponent.mailgunEmailAccount.getValue()}@${emailComponent.mailgunDomain.getValue()}>")
        formData.put("to", to)
        formData.add("subject", subject)
        formData.add("html", content)

        val request = client.method(HttpMethod.POST)
            .uri("${emailComponent.mailgunDomain.getValue()}/messages")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(formData)

        retrieve(request, to, subject, content, responsible)
    }

    private fun retrieve(
        request: WebClient.RequestHeadersSpec<*>,
        to: List<String>,
        subject: String,
        content: String,
        responsible: CmschUser?
    ) {
        try {
            val response = request.retrieve().toEntity(String::class.java).block()

            val action = "Mail sent to:$to subject:$subject text:'${
                content
                    .replace("\n", "")
                    .replace("\r", "")
            }' response:'${response}'"
            log.info(action)
            if (responsible != null) {
                auditLogService.fine(responsible, "email", action)
            } else {
                auditLogService.system("email", action)
            }
        } catch (e: WebClientException) {
            val action = "Failed to send email exception:${e.message} to:$to subject:'$subject' text:'${
                content
                    .replace("\n", "")
                    .replace("\r", "")
            }'"
            log.error(action)
            if (responsible != null) {
                auditLogService.error(responsible, "email", action)
            } else {
                auditLogService.system("email", action)
            }
        }
    }

}