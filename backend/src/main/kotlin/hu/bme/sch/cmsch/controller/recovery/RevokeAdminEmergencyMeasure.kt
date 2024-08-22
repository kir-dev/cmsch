package hu.bme.sch.cmsch.controller.recovery

import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.util.transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition

private const val PERMISSIONS_AS_WELL = "permissionsAsWell"

@Service
class RevokeAdminEmergencyMeasure(
    private val platformTransactionManager: PlatformTransactionManager,
    private val userRepository: UserRepository
) : EmergencyMeasure {

    override val displayName: String = "Adminok elvétele"
    override val description: String = "Minden felhasználótól elveszi az admin és a fejlesztő rangot."
    override val order: Int = 2

    override fun getFields(user: CmschUser) = listOf(
        FormElement(
            fieldName = PERMISSIONS_AS_WELL, label = "Jogok ürítése",
            type = FormElementType.CHECKBOX,
            formatRegex = ".*", invalidFormatMessage = "", values = "",
            note = "Ez mindenkinek a jogosultságait klireseteli",
            required = true, permanent = false, defaultValue = "false"
        ),
    )

    override fun executeMeasure(user: CmschUser, index: Int, params: Map<String, String>): String {
        val clearPermissions = params.getOrDefault(PERMISSIONS_AS_WELL, "off").equals("on", ignoreCase = true)

        return platformTransactionManager.transaction(
            readOnly = false,
            isolation = TransactionDefinition.ISOLATION_REPEATABLE_READ
        ) {
            return@transaction if (clearPermissions) {
                val users = userRepository.findAllByRoleOrRoleOrPermissionsNot(RoleType.SUPERUSER, RoleType.ADMIN, "")
                users.forEach {
                    it.role = RoleType.STAFF
                    it.permissions = ""
                }
                userRepository.saveAll(users)
                DashboardPage.dashboardPage(EMERGENCY_MEASURE_VIEW, index, "${users.size} módosítva")

            } else {
                val users = userRepository.findAllByRoleOrRole(RoleType.SUPERUSER, RoleType.ADMIN)
                users.forEach {
                    it.role = RoleType.STAFF
                }
                userRepository.saveAll(users)
                DashboardPage.dashboardPage(EMERGENCY_MEASURE_VIEW, index, "${users.size} módosítva")
            }
        }
    }

}