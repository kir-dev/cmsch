package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.SimpleEntityPage
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/my-debts")
@ConditionalOnBean(DebtComponent::class)
class DebtsOfUserController(
    private val productService: ProductService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : SimpleEntityPage<SoldProductEntity>(
    "my-debts",
    SoldProductEntity::class, ::SoldProductEntity,
    "Saját tartozásaim", "Saját tartozásaim",
    "Ezekkel a tételekkel a reszortgdaságisnak kell elszámolnod! A pontos módról emailben értesülhetsz.",

    { user -> productService.getAllDebtsByUser(user) },

    permission = PERMISSION_IMPLICIT_ANYONE,

    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    adminMenuIcon = "account_balance_wallet",
    adminMenuPriority = 6,
)

