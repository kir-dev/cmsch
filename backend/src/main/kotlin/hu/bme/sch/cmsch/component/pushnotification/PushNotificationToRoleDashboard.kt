package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.urlEncode
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

private const val VIEW = "pushnotification-to-role"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationToRoleDashboard(
    adminMenuService: AdminMenuService,
    component: PushNotificationComponent,
    private val auditLogService: AuditLogService,
    private val notificationService: PushNotificationService,
) : DashboardPage(
    VIEW,
    "Push értesítés jogosultságkörnek",
    "Push Értesítés küldése adott jogosultsággal rendelkezőknek",
    false,
    adminMenuService,
    component,
    auditLogService,
    ControlPermissions.PERMISSION_SEND_NOTIFICATIONS,
    adminMenuCategory = null,
    "send",
    5
) {

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            permissionCard,
            getUserNotificationForm(requestParams),
        )
    }

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak a megnyitásához.",
        wide = false
    )

    fun getUserNotificationForm(requestParams: Map<String, String>): DashboardFormCard {
        return DashboardFormCard(
            2,
            false,
            "Push Értesítés Jogosultságköröknek",
            "Értesítés küldése egy jogosultságkörbe tartozó felhasználóknak. Egy felhasználó csak akkor kapja meg az értesítést, ha engedélyezte azokat.",
            listOf(
                FormElement(
                    "role", "Jogosultság", FormElementType.SELECT,
                    ".*", "", getAllRoles(),
                    "A jogosultságkör, mely felhasználóinak értesítést akarsz küldeni",
                    required = true, defaultValue = requestParams.getOrDefault("role", "")
                ),
                FormElement(
                    "title", "Cím", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítés címe",
                    required = true, permanent = false, defaultValue = requestParams.getOrDefault("title", "")
                ),
                FormElement(
                    "body", "Üzenet", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítés szövege",
                    required = true, permanent = false, defaultValue = requestParams.getOrDefault("body", "")
                ),
                FormElement(
                    "image", "Kép", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítésben megjelenő kép URL-je (opcionális)",
                    required = false, permanent = false, defaultValue = requestParams.getOrDefault("image", "")
                ),
                FormElement(
                    "url", "Link", FormElementType.TEXT,
                    ".*", "", "",
                    "Ez a link nyílik meg, amikor a felhasználó az értesítésre kattint (opcionális)",
                    required = false, permanent = false, defaultValue = requestParams.getOrDefault("url", "")
                ),
            ),
            buttonCaption = "Küldés",
            buttonIcon = "send",
            action = "send-to-role",
            method = "post"
        )
    }

    @PostMapping("/send-to-role")
    fun send(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val roleString = allRequestParams["role"] ?: throw IllegalStateException("The role must be provided")
        val roleValue = roleString.split(":")[0].toIntOrNull() ?: throw IllegalStateException("Invalid role format")
        val role = RoleType.fromValue(roleValue) ?: throw IllegalStateException("Invalid role value")
        val title = allRequestParams["title"] ?: throw IllegalStateException("The title must be provided!")
        val body = allRequestParams["body"] ?: throw IllegalStateException("The body must be provided!")
        val image = allRequestParams["image"]
        val url = allRequestParams["url"]
        auditLogService.fine(
            user,
            "pushnotification",
            "sent a push notification to role: ${role.displayName} title: $title, body: $body, image: $image url: $url"
        )

        val count = notificationService.sendToRole(
            role,
            CmschNotification(title = title, body = body, image = image, link = url)
        )

        val card = "2"
        val params = HashMap<String, String>()
        params.putAll(allRequestParams)
        params["message"] = "Értesítés elküldve $count eszközre"
        params["card"] = card
        return "redirect:/admin/control/$VIEW?${params.urlEncode()}#${card}"
    }

    private fun getAllRoles(): String =
        RoleType.entries
            .sortedBy { it.value }
            .joinToString(",") {
                "${it.value}: ${it.name.replace(',', ' ')}"
            }
}
