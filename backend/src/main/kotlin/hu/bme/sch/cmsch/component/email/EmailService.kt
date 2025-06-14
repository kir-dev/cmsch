package hu.bme.sch.cmsch.component.email

import com.github.mustachejava.DefaultMustacheFactory
import hu.bme.sch.cmsch.component.login.CmschUser
import org.slf4j.LoggerFactory
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
    emailProviders: List<EmailProvider>,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val emailProviders = emailProviders.associateBy { it.getProviderName() }
    private val selectedProvider get() = emailProviders[emailComponent.emailProvider.getValue()]
    private val mustacheFactory = DefaultMustacheFactory()

    @Async
    fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        selectedProvider?.sendTextEmail(responsible, subject, content, to) ?: run {
            log.info("Unknown provider ${emailComponent.emailProvider.getValue()}")
        }
    }

    @Async
    fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        selectedProvider?.sendHtmlEmail(responsible, subject, content, to) ?: run {
            log.info("Unknown provider ${emailComponent.emailProvider.getValue()}")
        }
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
