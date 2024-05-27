package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.CmschNotification
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.urlEncode
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


private const val VIEW = "pushnotification-to-user"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(PushNotificationComponent::class)
class PushNotificationToUserDashboard(
    adminMenuService: AdminMenuService,
    component: PushNotificationComponent,
    private val auditLogService: AuditLogService,
    private val notificationService: PushNotificationService,
    private val userService: UserService
) : DashboardPage(
    VIEW,
    "Push értesítés felhasználónak",
    "Push Értesítés küldése felhasználónak",
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
            getAllUserNotificationForm(requestParams),
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
            "Push Értesítés Felhasználónak",
            "Értesítés küldése egy felhasználónak. A felhasználó csak akkor kapja meg az értesítést, ha engedélyezte azokat.",
            listOf(
                FormElement(
                    "userId", "Felhasználó", FormElementType.SEARCHABLE_SELECT,
                    ".*", "", getUserList(),
                    "Akinek az értesítést küldöd",
                    required = true, defaultValue = requestParams.getOrDefault("userId", "")
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
            action = "send-to-user",
            method = "post"
        )
    }

    fun getAllUserNotificationForm(requestParams: Map<String, String>): DashboardFormCard {
        return DashboardFormCard(
            3,
            false,
            "Push Értesítés Az Összes Felhasználónak",
            "Értesítés küldése az összes felhasználónak. Egy felhasználó csak akkor kapja meg az értesítést, ha engedélyezte azokat. Ezzel ÓVATOSAN, nem fogják szeretni, ha spammeled őket!",
            listOf(
                FormElement(
                    "allTitle", "Cím", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítés címe",
                    required = true, permanent = false, defaultValue = requestParams.getOrDefault("allTitle", "")
                ),
                FormElement(
                    "allBody", "Üzenet", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítés szövege",
                    required = true, permanent = false, defaultValue = requestParams.getOrDefault("allBody", "")
                ),
                FormElement(
                    "allImage", "Kép", FormElementType.TEXT,
                    ".*", "", "",
                    "Az értesítésben megjelenő kép URL-je (opcionális)",
                    required = false, permanent = false, defaultValue = requestParams.getOrDefault("allImage", "")
                ),
                FormElement(
                    "allUrl", "Link", FormElementType.TEXT,
                    ".*", "", "",
                    "Ez a link nyílik meg, amikor a felhasználó az értesítésre kattint (opcionális)",
                    required = false, permanent = false, defaultValue = requestParams.getOrDefault("allUrl", "")
                ),
            ),
            buttonCaption = "Küldés",
            buttonIcon = "send",
            action = "send-to-all-users",
            method = "post"
        )
    }

    @PostMapping("/send-to-all-users")
    fun sendToAll(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val title = allRequestParams["allTitle"] ?: throw IllegalStateException("The title must be provided!")
        val body = allRequestParams["allBody"] ?: throw IllegalStateException("The body must be provided!")
        val image = allRequestParams["allImage"]
        val url = allRequestParams["allUrl"]
        auditLogService.fine(
            user,
            "pushnotification",
            "sent a push notification to all users: title: $title, body: $body, image: $image url:$url"
        )

        val count = notificationService.sendToAllUsers(
            CmschNotification(title = title, body = body, image = image, link = url)
        )

        val params = HashMap<String, String>()
        params.putAll(allRequestParams)
        params["message"] = "Értesítés elküldve $count eszközre"
        params["card"] = "3"
        return "redirect:/admin/control/$VIEW?${params.urlEncode()}"
    }

    @PostMapping("/send-to-user")
    fun send(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val userIdString = allRequestParams.getOrDefault("userId", "")
        val authorId = userIdString.split(":")[0].toIntOrNull() ?: throw IllegalStateException("Invalid userId format")
        val title = allRequestParams["title"] ?: throw IllegalStateException("The title must be provided!")
        val body = allRequestParams["body"] ?: throw IllegalStateException("The body must be provided!")
        val image = allRequestParams["image"]
        val url = allRequestParams["url"]
        auditLogService.fine(
            user,
            "pushnotification",
            "sent a push notification to user: title: $title, body: $body, image: $image url:$url to user: $authorId"
        )

        val count = notificationService.sendToUser(
            authorId,
            CmschNotification(title = title, body = body, image = image, link = url)
        )

        val params = HashMap<String, String>()
        params.putAll(allRequestParams)
        params["message"] = "Értesítés elküldve $count eszközre"
        params["card"] = "2"
        return "redirect:/admin/control/$VIEW?${params.urlEncode()}"
    }

    private fun getUserList(): String =
        userService.findAllUserSelectorView()
            .sortedBy { it.name }
            .joinToString(",") {
                "${it.id}: ${it.fullNameWithAlias.replace(',', ' ')} [${it.provider.firstOrNull() ?: 'n'}]"
            }
}
