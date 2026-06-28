package hu.bme.sch.cmsch.component.email

import com.github.mustachejava.DefaultMustacheFactory
import hu.bme.sch.cmsch.component.login.CmschUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.StringWriter
import java.sql.SQLException

@Service
@ConditionalOnBean(EmailComponent::class)
class EmailService(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailComponent: EmailComponent,
    private val kirMail: KirMailEmailProvider,
    private val mailgun: MailgunEmailProvider
) {

    private val mustacheFactory = DefaultMustacheFactory()
    private val selectedProvider
        get() = when (emailComponent.provider) {
            EmailProviderType.KIR_MAIL -> kirMail
            EmailProviderType.MAILGUN -> mailgun
        }

    @Async
    @Retryable(maxRetries = 5, delay = 500L, multiplier = 1.5)
    fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        selectedProvider.sendTextEmail(responsible, subject, content, to)
    }

    @Async
    @Retryable(maxRetries = 5, delay = 500L, multiplier = 1.5)
    fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        selectedProvider.sendHtmlEmail(responsible, subject, content, to)
    }

    @Transactional(readOnly = true)
    fun getTemplateBySelector(selector: String): EmailTemplateEntity? {
        return emailTemplateRepository.findTop1BySelector(selector).firstOrNull()
    }

    fun sendTemplatedEmail(
        responsible: CmschUser?,
        template: EmailTemplateEntity,
        values: Map<String, String>,
        to: List<String>,
        subjectOverride: String? = null,
        rawValues: Map<String, String> = emptyMap()
    ) {
        val content = fillOutText(template.template, template.selector, values, rawValues)
        val subject = fillOutText(subjectOverride ?: template.subject, "${template.selector}_subject", values, rawValues)
        return when (template.mode) {
            EmailMode.TEXT -> sendTextEmail(responsible, subject, content, to)
            EmailMode.HTML -> sendHtmlEmail(responsible, subject, content, to)
        }
    }

    private fun fillOutText(text: String, cacheKey: String, values: Map<String, String>, rawValues: Map<String, String>): String {
        var result = text
        for ((key, value) in rawValues) {
            result = result.replace("{{$key}}", value)
        }
        val mustache = mustacheFactory.compile(result.reader(), cacheKey)
        val writer = StringWriter()
        mustache.execute(writer, values).flush()
        return writer.toString()
    }

}
