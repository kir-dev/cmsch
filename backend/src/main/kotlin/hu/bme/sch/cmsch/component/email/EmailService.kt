package hu.bme.sch.cmsch.component.email

import com.github.mustachejava.DefaultMustacheFactory
import hu.bme.sch.cmsch.component.login.CmschUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.StringWriter

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
    fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        selectedProvider.sendTextEmail(responsible, subject, content, to)
    }

    @Async
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
        to: List<String>
    ) {
        val content = fillOutEmailTemplate(template, values)
        return when (template.mode) {
            EmailMode.TEXT -> sendTextEmail(responsible, template.subject, content, to)
            EmailMode.HTML -> sendHtmlEmail(responsible, template.subject, content, to)
        }
    }

    private fun fillOutEmailTemplate(template: EmailTemplateEntity, values: Map<String, String>): String {
        val mustache = mustacheFactory.compile(template.template.reader(), template.selector)
        val writer = StringWriter()
        mustache.execute(writer, values).flush()
        return writer.toString()
    }

}
