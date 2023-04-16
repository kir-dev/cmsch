package hu.bme.sch.cmsch.component.bmejegy

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
@RequestMapping("/admin/control/bmejegy-tickets")
@ConditionalOnBean(BmejegyComponent::class)
class BmejegyController(
    repo: BmejegyRecordRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BmejegyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<BmejegyRecordEntity>(
    "bmejegy-tickets",
    BmejegyRecordEntity::class, ::BmejegyRecordEntity,
    "Jegy", "Jegyek",
    "Ebben a menüben a szinkrizált Bmejegy példányok láthatóak. Az értékek módosíthatóak, mert " +
            "csak az új példányokat húzza be a szinkronizáló mindig.",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_SHOW_BME_TICKET,
    createPermission = StaffPermissions.PERMISSION_CREATE_BME_TICKET,
    editPermission =   StaffPermissions.PERMISSION_EDIT_BME_TICKET,
    deletePermission = StaffPermissions.PERMISSION_DELETE_BME_TICKET,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "local_activity",
    adminMenuPriority = 1,
)
