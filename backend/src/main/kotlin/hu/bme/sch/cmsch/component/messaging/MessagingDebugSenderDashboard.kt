package hu.bme.sch.cmsch.component.messaging

import hu.bme.sch.cmsch.admin.dashboard.*
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
import java.util.*

private const val VIEW = "messaging"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(MessagingComponent::class)
class MessagingDebugSenderDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: MessagingComponent,
    private val auditLogService: AuditLogService,
    private val messagingService: MessagingService
) : DashboardPage(
    VIEW,
    "Direkt üzenet",
    "Direkt üzenet küldése egy proxyn keresztül Discord felhasználóknak",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    ControlPermissions.PERMISSION_SEND_MESSAGE,
    adminMenuCategory = null,
    "send",
    5
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
            "Ezzel az eszközzel tudsz teszt üzenetet küldeni. " +
                    "A proxy majd továbbítja a megfelelő embereknek. " +
                    "Fontos, hogy először a címzetteknek össze kell szinkronizálni az authschjukat a " +
                    "Discord fiókkal a Discoordinator app segítségével.",
            listOf(
                FormElement(
                    "api", "API URL", FormElementType.TEXT,
                    ".*", "", "",
                    "Itt érhető el az üzenet proxy",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "token", "Token", FormElementType.TEXT,
                    ".*", "", "",
                    "Ez a token azonosítja majd a szervert",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "target", "Címzettek", FormElementType.TEXT,
                    ".*", "", "",
                    "PéK internalId-k vesszővel felsorolva",
                    required = true, permanent = false, defaultValue = ""
                ),
                FormElement(
                    "message", "Üzenet", FormElementType.LONG_TEXT,
                    ".*", "", "",
                    "Ezt fogja majd elküldeni a szerver",
                    required = true, permanent = false, defaultValue = ""
                )
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
        val targets = allRequestParams.getOrDefault("target", "").split(", *".toRegex())
        auditLogService.fine(user, "messaging",
            "sent simple debug message '${message.replace("\n", "").replace("\r", "")}' to targets: [${targets.joinToString(", ")}]")

        messagingService.sendSimpleMessage(
            targets,
            message,
            allRequestParams.getOrDefault("api", ""),
            allRequestParams.getOrDefault("token", "")
        )
        return "redirect:/admin/control/$VIEW"
    }

}