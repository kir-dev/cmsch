package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/debts")
@ConditionalOnBean(DebtComponent::class)
class SoldProductController(
    repo: SoldProductRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    private val clock: TimeService
) : OneDeepEntityPage<SoldProductEntity>(
    "debts",
    SoldProductEntity::class, ::SoldProductEntity,
    "Tranzakció", "Tranzakciók",
    "Az összes eladásból származó tranzakciók.",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_SHOW_DEBTS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   StaffPermissions.PERMISSION_EDIT_DEBTS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_DEBTS,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "sync_alt",
    adminMenuPriority = 2,
) {

    override fun onEntityPreSave(entity: SoldProductEntity, auth: Authentication): Boolean {
        val date = clock.getTimeInSeconds()
        val user = auth.getUserFromDatabase()
        entity.log = "${entity.log} '${user.fullName}'(${user.id}) changed [shipped: ${entity.shipped}, payed: ${entity.payed}, finsihed: ${entity.finsihed}] at $date;"
        return true
    }

}
