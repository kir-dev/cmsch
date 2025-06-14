package hu.bme.sch.cmsch.component.email

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AuditLogService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import java.io.IOException

@Service
@ConditionalOnBean(EmailComponent::class)
class KirMailEmailProvider(
    private val emailComponent: EmailComponent,
    private val auditLogService: AuditLogService,
) : EmailProvider {

    private val log = LoggerFactory.getLogger(javaClass)
    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()
    private val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

    override fun getProviderName() = "kirmail"

    override fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        if (!emailComponent.enableKirMail)
            return

        logSend(to, subject, content, responsible)

        for (target in to) {
            val requestBody = KirMailEmail(
                from = KirMailFrom(
                    name = emailComponent.kirmailAccountName,
                    email = emailComponent.kirmailEmailAddress,
                ),
                to = target,
                subject = subject,
                html = content,
                replyTo = emailComponent.kirmailReplyTo,
                queue = emailComponent.kirmailQueue,
            )

            val jsonRequestBody = objectMapper.writeValueAsString(requestBody)
            val body = jsonRequestBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://mail.kir-dev.hu/api/send")
                .post(body)
                .addHeader(HttpHeaders.AUTHORIZATION, "Api-Key ${emailComponent.kirmailToken}")
                .addHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .build()

            retrieve(request, target, subject, content, responsible)
        }
    }

    override fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        if (!emailComponent.enableKirMail)
            return

        logSend(to, subject, content, responsible)

        for (target in to) {
            val requestBody = KirMailEmail(
                from = KirMailFrom(
                    name = emailComponent.kirmailAccountName,
                    email = emailComponent.kirmailEmailAddress,
                ),
                to = target,
                subject = subject,
                html = content,
                replyTo = emailComponent.kirmailReplyTo,
                queue = emailComponent.kirmailQueue,
            )

            val jsonRequestBody = objectMapper.writeValueAsString(requestBody)
            val body = jsonRequestBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://mail.kir-dev.hu/api/send")
                .post(body)
                .addHeader(HttpHeaders.AUTHORIZATION, "Api-Key ${emailComponent.kirmailToken}")
                .addHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
                .build()

            retrieve(request, target, subject, content, responsible)
        }
    }

    private fun logSend(
        to: List<String>,
        subject: String,
        content: String,
        responsible: CmschUser?
    ) {
        val action = "Mail sent to:$to subject:$subject text:'${
            content
                .replace("\n", "")
                .replace("\r", "")
        }'"
        log.info(action)
        if (responsible != null) {
            auditLogService.fine(responsible, "email", action)
        } else {
            auditLogService.system("email", action)
        }
    }

    private fun retrieve(
        request: Request,
        to: String,
        subject: String,
        content: String,
        responsible: CmschUser?
    ) {
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
            }
        } catch (e: IOException) {
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

    data class KirMailEmail(
        val from: KirMailFrom,
        val to: String,
        val subject: String,
        val html: String,
        val replyTo: String,
        val queue: String
    )

    data class KirMailFrom(
        val name: String,
        val email: String
    )

}
