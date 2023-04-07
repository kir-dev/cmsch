package hu.bme.sch.cmsch.component.qrfight

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
@RequestMapping("/admin/control/qr-levels")
@ConditionalOnBean(QrFightComponent::class)
class QrLevelController(
    repo: QrLevelRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: QrFightComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<QrLevelEntity>(
    "qr-levels",
    QrLevelEntity::class, ::QrLevelEntity,
    "Szint", "Szintek",
    "QR Fight szintek hozzáadása és szerkesztése",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_QR_FIGHT,
    createPermission = StaffPermissions.PERMISSION_EDIT_QR_FIGHT,
    editPermission =   StaffPermissions.PERMISSION_EDIT_QR_FIGHT,
    deletePermission = StaffPermissions.PERMISSION_EDIT_QR_FIGHT,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "apartment",
    adminMenuPriority = 1,
)

