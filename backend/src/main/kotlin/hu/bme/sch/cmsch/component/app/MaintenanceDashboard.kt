package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

private const val SURE = "sure"

@Controller
@RequestMapping("/admin/control/${MaintenanceDashboard.VIEW}")
@ConditionalOnBean(ApplicationComponent::class)
class MaintenanceDashboard(
    private val adminMenuService: AdminMenuService,
    appComponent: ApplicationComponent,
    private val auditLogService: AuditLogService,
    private val context: ConfigurableApplicationContext,
) : DashboardPage(
    view = VIEW,
    title = "Karbantartás",
    description = "Itt tudod pl. redeployolni. Ha nem tudod mi ez és ide kerültél, hagyd el az oldalt és ne egyre vissza!",
    wide = false,
    adminMenuService = adminMenuService,
    component = appComponent,
    auditLog = auditLogService,
    showPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    adminMenuCategory = ApplicationComponent.DEVELOPER_CATEGORY,
    adminMenuIcon = "power_settings_new",
    adminMenuPriority = 10
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val VIEW = "superuser-maintenance"
    }

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = wide
    )

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            getShutdownComponent(user),
            permissionCard,
        )
    }

    fun getShutdownComponent(user: CmschUser): DashboardFormCard {
        return DashboardFormCard(
            id = 2,
            wide = false,
            title = "Szerver leállítása",
            description = "Redeploy miatt kényelmes lehet innen leállítani a szervert.",
            content = listOf(
                FormElement(
                    fieldName = SURE, label = "Biztos vagy benne?", type = FormElementType.CHECKBOX,
                    formatRegex = ".*", invalidFormatMessage = "", values = "",
                    note = "Ha ezt bepipálod, akkor le fog állni a szerver.",
                    required = true, permanent = false, defaultValue = "false"
                ),
            ),
            buttonCaption = "LEÁLLÍTÁS",
            buttonIcon = "power_settings_new",
            action = "shutdown",
            method = "post",
        )
    }

    @PostMapping("/shutdown")
    fun shutdown(auth: Authentication, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        auditLogService.system("admin", "${user.userName} #${user.id} scheduled a shutdown!")

        if (allRequestParams.getOrDefault(SURE, "off").equals("on", ignoreCase = true)) {
            log.warn("${user.userName} #${user.id} scheduled a shutdown!")
            context.close()
        }

        return dashboardPage(view = VIEW, card = 2, message = "Távesz baktalé!")
    }

}