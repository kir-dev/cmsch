package hu.bme.sch.cmsch.controller.dashboard

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardTableCard
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.transaction
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/admin/control/role-info")
class RoleControlDashboard(
    adminMenuService: AdminMenuService,
    applicationComponent: ApplicationComponent,
    auditLogService: AuditLogService,
    private val transactionManager: PlatformTransactionManager,
    private val userRepository: UserRepository
) : DashboardPage(
    "role-info",
    "Jogosultságok",
    "Kinek milyen jogosultságai vannak",
    false,
    adminMenuService,
    applicationComponent,
    auditLogService,
    ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    ApplicationComponent.DEVELOPER_CATEGORY,
    "perm_scan_wifi",
    7
) {

    private val permissionCard = DashboardPermissionCard(
        3,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        val users = transactionManager.transaction(readOnly = true) { userRepository.findAll() }
            .filter { it.role.value >= RoleType.STAFF.value }
        return listOf(
            permissionCard,
            getSuperusers(users),
            getAdmins(users),
            getStaffs(users)
        )
    }

    private fun getSuperusers(users: List<UserEntity>) = DashboardTableCard(
        0,
        "Fejlesztők",
        "",
        listOf("Id", "Név"),
        users.filter { it.role == RoleType.SUPERUSER }
            .map {
                listOf(it.id.toString(), it.fullNameWithAlias)
            },
        false,
        exportable = false
    )

    private fun getAdmins(users: List<UserEntity>) = DashboardTableCard(
        1,
        "Adminok",
        "",
        listOf("Id", "Név"),
        users.filter { it.role == RoleType.ADMIN }
            .map {
                listOf(it.id.toString(), it.fullNameWithAlias)
            },
        false,
        exportable = false
    )

    private fun getStaffs(users: List<UserEntity>) = DashboardTableCard(
        2,
        "Rendezők",
        "",
        listOf("Id", "Név"),
        users.filter { it.role == RoleType.STAFF }
            .map {
                listOf(it.id.toString(), it.fullNameWithAlias)
            },
        false,
        exportable = false
    )

}