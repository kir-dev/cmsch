package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.login.CmschUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(EmailComponent::class)
open class EmailService(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailComponent: EmailComponent,
    emailProviders: List<EmailProvider>,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val emailProviders = emailProviders.associateBy { it.getProviderName() }

    @Async
    open fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        emailProviders[emailComponent.emailProvider.getValue()]?.sendTextEmail(responsible, subject, content, to)
            ?: run {
                log.info("Unknown provider ${emailComponent.emailProvider.getValue()}")
            }
    }

    @Async
    open fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        emailProviders[emailComponent.emailProvider.getValue()]?.sendHtmlEmail(responsible, subject, content, to)
            ?: run {
                log.info("Unknown provider ${emailComponent.emailProvider.getValue()}")
            }
    }

    @Transactional(readOnly = true)
    open fun getTemplateBySelector(selector: String): EmailTemplateEntity? {
        return emailTemplateRepository.findTop1BySelector(selector).firstOrNull()
    }

}