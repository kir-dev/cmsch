package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException

@Controller
@RequestMapping("/admin/control/riddle-ms")
@ConditionalOnBean(RiddleComponent::class)
class RiddleMicroserviceDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: RiddleComponent,
    private val auditLogService: AuditLogService,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val riddleComponent: RiddleComponent
) : DashboardPage(
    "riddle-ms",
    "Riddle MS dashboard",
    "Riddle microservice konfigurációs panel",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    ControlPermissions.PERMISSION_CONTROL_RIDDLE,
    adminMenuCategory = null,
    adminMenuIcon = "tune",
    adminMenuPriority = 5,
    ignoreFromMenu = !startupPropertyConfig.riddleMicroserviceEnabled
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        return listOf(
            permissionCard,
            pingForm(),
            reloadComponentConfigForm(),
            reloadRiddleAndCategoryCache(),
            reloadAllForm(),
            saveAllForm(),
            forceUnlockEverythingForm(),
        )
    }

    private fun reloadComponentConfigForm(): DashboardFormCard {
        return DashboardFormCard(
            2,
            false,
            "Komponens config újratöltése",
            "A riddle komponens konfigurációjának frissítése a riddle nodeon",
            listOf(),
            buttonCaption = "Újratöltés",
            buttonIcon = "start",
            action = "reload-component-config",
            method = "post"
        )
    }

    @PostMapping("/reload-component-config")
    fun reloadComponentConfigPost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("reload-component-config", user)

        return "redirect:/admin/control/riddle-ms?card=2&message=$status"
    }

    private fun reloadRiddleAndCategoryCache(): DashboardFormCard {
        return DashboardFormCard(
            3,
            false,
            "Riddle és kategória cache újratöltése",
            "Riddle és Riddle kategória cache frissítése a riddle nodeon",
            listOf(),
            buttonCaption = "Frissítés",
            buttonIcon = "start",
            action = "reload-riddle-and-category-cache",
            method = "post"
        )
    }

    @PostMapping("/reload-riddle-and-category-cache")
    fun reloadRiddleAndCategoryCachePost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("reload-riddle-and-category-cache", user)

        return "redirect:/admin/control/riddle-ms?card=3&message=$status"
    }

    private fun reloadAllForm(): DashboardFormCard {
        return DashboardFormCard(
            4,
            false,
            "Teljes cache ürítése",
            "A teljes riddle cache frissítése a riddle nodeon. Ettől lehet, hogy adatvesztés fog történni!",
            listOf(),
            buttonCaption = "Újratöltés",
            buttonIcon = "start",
            action = "reload-all",
            method = "post"
        )
    }

    @PostMapping("/reload-all")
    fun reloadAllPost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("reload-all", user)

        return "redirect:/admin/control/riddle-ms?card=4&message=$status"
    }

    private fun saveAllForm(): DashboardFormCard {
        return DashboardFormCard(
            5,
            false,
            "Minden adat lementése",
            "A riddle cache lementése a riddle nodeon",
            listOf(),
            buttonCaption = "Minden mentése",
            buttonIcon = "start",
            action = "save-all",
            method = "post"
        )
    }

    @PostMapping("/save-all")
    fun saveAllPost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("save-all", user)

        return "redirect:/admin/control/riddle-ms?card=5&message=$status"
    }

    private fun forceUnlockEverythingForm(): DashboardFormCard {
        return DashboardFormCard(
            6,
            false,
            "Komponens config újratöltése",
            "Az összes lock felengedése",
            listOf(),
            buttonCaption = "Felengedés",
            buttonIcon = "start",
            action = "force-unlock-everything",
            method = "post"
        )
    }

    @PostMapping("/force-unlock-everything")
    fun forceUnlockEverythingPost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("force-unlock-everything", user)

        return "redirect:/admin/control/riddle-ms?card=6&message=$status"
    }

    private fun pingForm(): DashboardFormCard {
        return DashboardFormCard(
            7,
            false,
            "Ping",
            "Kapcsolat tesztelése",
            listOf(),
            buttonCaption = "PING",
            buttonIcon = "network_ping",
            action = "ping",
            method = "post"
        )
    }

    @PostMapping("/ping")
    fun pingPost(auth: Authentication): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val status = sendRequest("ping", user)

        return "redirect:/admin/control/riddle-ms?card=7&message=$status"
    }

    private fun sendRequest(path: String, user: CmschUser?): String {
        val client = WebClient.builder()
            .baseUrl(riddleComponent.microserviceNodeBaseUrl.getValue())
            .defaultHeaders { header -> header.add("token", startupPropertyConfig.managementToken) }
            .build()

        val request = client.method(HttpMethod.POST)
            .uri("${riddleComponent.microserviceNodeBaseUrl.getValue()}/remote-api/riddle/${path}")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)

        return retrieve(request, path, user)
    }

    private fun retrieve(
        request: WebClient.RequestHeadersSpec<*>,
        to: String,
        responsible: CmschUser?
    ): String {
        try {
            val response = request.retrieve().toEntity(String::class.java).block()?.body ?: "NO_ANSWER"

            val action = "Remote command sent to:$to response:$response"
            log.info(action)
            if (responsible != null) {
                auditLogService.fine(responsible, "riddle", action)
            } else {
                auditLogService.system("riddle", action)
            }
            return response
        } catch (e: WebClientException) {
            val action = "Remote command failed to send to:$to e:${e.message}"
            log.error(action)
            if (responsible != null) {
                auditLogService.error(responsible, "riddle", action)
            } else {
                auditLogService.system("riddle", action)
            }
        }
        return "ERROR_SEE_LOG"
    }

}

