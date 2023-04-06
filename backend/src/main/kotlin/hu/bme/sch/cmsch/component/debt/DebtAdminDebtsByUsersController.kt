package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_DEBTS
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/control/debts-by-users")
@ConditionalOnBean(DebtComponent::class)
class DebtAdminDebtsByUsersController(
    private val soldProductRepository: SoldProductRepository,
    private val groupRepository: GroupRepository,
    private val clock: TimeService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : TwoDeepEntityPage<DebtsByUserVirtualEntity, SoldProductEntity>(
    "debts-by-users",
    DebtsByUserVirtualEntity::class,
    SoldProductEntity::class, ::SoldProductEntity,
    "Tartozás", "Felhasználó tartozásai",
    "Tartozások felhasználónként csoportosítva",

    object : ManualRepository<DebtsByUserVirtualEntity, Int>() {
        override fun findAll(): Iterable<DebtsByUserVirtualEntity> {
            return soldProductRepository.findAll().groupBy { it.ownerId }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].responsibleGroupId).map { it.name }.orElse("n/a")
                    DebtsByUserVirtualEntity(
                        it[0].ownerId,
                        it[0].ownerName,
                        groupName,
                        it.sumOf { it.price },
                        it.filter { !it.payed }.sumOf { it.price },
                        it.filter { !it.finsihed }.sumOf { it.price }
                    )
                }
        }

    },
    soldProductRepository,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_DEBTS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   PERMISSION_EDIT_DEBTS,
    deletePermission = ImplicitPermissions.PERMISSION_NOBODY,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = false,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "account_balance",
    adminMenuPriority = 12,
) {

    override fun fetchSublist(id: Int): Iterable<SoldProductEntity> {
        return soldProductRepository.findAllByOwnerId(id)
    }

    override fun onEntityPreSave(entity: SoldProductEntity, auth: Authentication): Boolean {
        val date = clock.getTimeInSeconds()
        val user = auth.getUserFromDatabase()
        entity.log = "${entity.log} '${user.fullName}'(${user.id}) changed [shipped: ${entity.shipped}, payed: ${entity.payed}, finished: ${entity.finsihed}] at $date;"
        return true
    }

}
