package hu.bme.sch.cmsch.controller.dashboard

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardTableCard
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@RequestMapping("/admin/control/debug-dashboard")
class DebugDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: ApplicationComponent,
    auditLogService: AuditLogService,
    private val transactionManager: PlatformTransactionManager,
) : DashboardPage(
    "debug-dashboard",
    "Debug Dashboard",
    "Dolgok kipróbálására készített dashboard",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    ApplicationComponent.DEVELOPER_CATEGORY,
    "bug_report",
    100
) {

    private val permissionCard = DashboardPermissionCard(
        3,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        return listOf(
            permissionCard,
            getTable(),
        )
    }

    private fun getTable() = DashboardTableCard(
        0,
        "Card",
        "",
        listOf("Col", "Col"),
        listOf(
            listOf("", "")
        ),
        false,
        exportable = false
    )

    @ResponseBody
    @GetMapping("/headers")
    fun fetchHeaders(httpServletRequest: HttpServletRequest): String {
        for (headerName in httpServletRequest.headerNames) {
            println("$headerName to ${httpServletRequest.getHeader(headerName)}")
        }
        return "ok"
    }

}