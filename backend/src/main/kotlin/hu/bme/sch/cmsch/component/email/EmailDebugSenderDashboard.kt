package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/control/email-sender")
@ConditionalOnBean(EmailComponent::class)
class EmailDebugSenderDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: EmailComponent,
    auditLogService: AuditLogService,
    private val emailService: EmailService
) : DashboardPage(
    "email-sender",
    "Email küldés",
    "Email küldés tesztelése",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    ControlPermissions.PERMISSION_SEND_EMAIL,
    adminMenuCategory = null,
    "send",
    2
) {

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        return listOf(
            permissionCard,
            getForm(),
        )
    }

    fun getForm(): DashboardFormCard {
        return DashboardFormCard(
            2,
            false,
            "Üzenet küldése",
            "Ezzel az eszközzel tudsz teszt emailt küldeni. " +
                    "A szerver majd a beállítások alapján elküldi a címzettnek.",
            listOf(
                FormElement(
                    "target", "Címzett", FormElementType.TEXT,
                    ".*", "", "",
                    "Email cím",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "subject", "Tárgy", FormElementType.TEXT,
                    ".*", "", "",
                    "Az üzenet tárgya",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "message", "Üzenet", FormElementType.LONG_TEXT,
                    ".*", "", "",
                    "Ezt fogja majd elküldeni emailben",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "html", "HTML üzenet", FormElementType.CHECKBOX,
                    ".*", "", "",
                    "Ha be van pipálva, akkor HTML-ként küldi el",
                    required = true, permanent = false, defaultValue = ""
                ),
            ),
            buttonCaption = "Küldés",
            buttonIcon = "send",
            action = "send",
            method = "post"
        )
    }

    @PostMapping("/send")
    fun send(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val message = allRequestParams.getOrDefault("message", "")
        val target = allRequestParams.getOrDefault("target", "").trim()
        val subject = allRequestParams.getOrDefault("subject", "").trim()
        val html = allRequestParams.getOrDefault("html", "off").equals("on", ignoreCase = true)

        if (html) {
            emailService.sendHtmlEmail(
                user,
                subject,
                message,
                listOf(target),
            )
        } else {
            emailService.sendTextEmail(
                user,
                subject,
                message,
                listOf(target),
            )
        }
        return "redirect:/admin/control/email-sender"
    }

}