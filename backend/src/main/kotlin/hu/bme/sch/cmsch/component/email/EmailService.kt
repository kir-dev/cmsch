package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.login.CmschUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(EmailComponent::class)
open class EmailService(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailComponent: EmailComponent,
    private val mailgunEmailService: MailgunEmailProvider
) {

    @Async
    open fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        if (emailComponent.enableMailgun.isValueTrue()) {
            mailgunEmailService.sendTextEmail(responsible, subject, content, to)
        }
    }

    @Async
    open fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>) {
        if (emailComponent.enableMailgun.isValueTrue()) {
            mailgunEmailService.sendHtmlEmail(responsible, subject, content, to)
        }
    }

    @Transactional(readOnly = true)
    open fun getTemplateBySelector(selector: String): EmailTemplateEntity? {
        return emailTemplateRepository.findTop1BySelector(selector).firstOrNull()
    }

}