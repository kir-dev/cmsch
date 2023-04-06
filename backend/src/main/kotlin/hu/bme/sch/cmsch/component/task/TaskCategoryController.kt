package hu.bme.sch.cmsch.component.task

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
@RequestMapping("/admin/control/task-categories")
@ConditionalOnBean(TaskComponent::class)
class TaskCategoryController(
    repo: TaskCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<TaskCategoryEntity>(
    "task-categories",
    TaskCategoryEntity::class, ::TaskCategoryEntity,
    "Feladat Kategória", "Feladat kategóriák",
    "Feladatok kategóriájának kezelése. A feladatok javítására használd a \"Feladatok értékelése\" menüt!",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES,
    createPermission = StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES,
    editPermission =   StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES,
    deletePermission = StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "category",
    adminMenuPriority = 2,
)
