package hu.bme.sch.cmsch.component.race

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
@RequestMapping("/admin/control/race-categories")
@ConditionalOnBean(RaceComponent::class)
class RaceCategoryController(
    repo: RaceCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<RaceCategoryEntity>(
    "race-categories",
    RaceCategoryEntity::class, ::RaceCategoryEntity,
    "Kategória", "Mérés kategóriák",
    "Időmérés extra kategóriái",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY,
    createPermission = StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY,
    editPermission =   StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY,
    deletePermission = StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "category",
    adminMenuPriority = 4,
)
