package hu.bme.sch.cmsch.controller.recovery

import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.PermissionsService
import hu.bme.sch.cmsch.util.transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition

@Service
class ReadOnlyAdminEmergencyMeasure(
    private val platformTransactionManager: PlatformTransactionManager,
    private val userRepository: UserRepository,
    private val permissionService: PermissionsService
) : EmergencyMeasure {

    override val displayName: String = "Csak olvasás"
    override val description: String = "Minden fejlesztőtől, admintól és stafftól elveszi az írási jogokat."
    override val order: Int = 1

    override fun getFields(user: CmschUser) = listOf<FormElement>()

    override fun executeMeasure(user: CmschUser, index: Int, params: Map<String, String>): String {
        return platformTransactionManager.transaction(
            readOnly = false,
            isolation = TransactionDefinition.ISOLATION_REPEATABLE_READ
        ) {
            val readOnlyPermissionsForAdmins = permissionService.allStaffPermissions
                    .filter { it.readOnly }
                    .joinToString(", ") { it.permissionString }

            val users = userRepository.findAllByRoleOrRoleOrPermissionsNot(RoleType.SUPERUSER, RoleType.ADMIN, "")
            users.forEach { user ->
                if (user.role == RoleType.ADMIN || user.role == RoleType.SUPERUSER) {
                    user.role = RoleType.STAFF
                    user.permissions = readOnlyPermissionsForAdmins
                } else {
                    user.permissions = user.permissions
                        .split(", *".toRegex())
                        .mapNotNull { permissionString ->
                            val permission = permissionService.allPermissions.firstOrNull { it.permissionString == permissionString }
                            if (permission == null || !permission.readOnly)
                                return@mapNotNull null
                            return@mapNotNull permission.permissionString
                        }
                        .joinToString(",")
                }
            }
            userRepository.saveAll(users)
            return@transaction DashboardPage.dashboardPage(EMERGENCY_MEASURE_VIEW, index, "${users.size} módosítva")
        }
    }

}