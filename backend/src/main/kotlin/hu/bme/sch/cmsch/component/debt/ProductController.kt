package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/products")
@ConditionalOnBean(DebtComponent::class)
class ProductController(
    repo: ProductRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<ProductEntity>(
    "products",
    ProductEntity::class, ::ProductEntity,
    "Termék", "Termékek",
    "Az összes vásárolható termék kezelése. Az eladáshoz külön felület tartozik!",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_PRODUCTS,
    createPermission = StaffPermissions.PERMISSION_EDIT_PRODUCTS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_PRODUCTS,
    deletePermission = StaffPermissions.PERMISSION_EDIT_PRODUCTS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "inventory_2",
    adminMenuPriority = 1,
)
