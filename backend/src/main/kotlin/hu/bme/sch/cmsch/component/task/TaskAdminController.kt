package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.component.token.TokenComponent
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT_DELETE
import hu.bme.sch.cmsch.model.GuildToUserMappingEntity
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TASKS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TASKS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/task")
@ConditionalOnBean(TaskComponent::class)
class TaskAdminController(
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    TaskComponent::class.java,
    component,
    PERMISSION_CONTROL_TASKS,
    "Feladatok",
    "Feladat beállítások",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/task")
@ConditionalOnBean(TaskComponent::class)
class TaskController(
    repo: TaskEntityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent
) : AbstractAdminPanelController<TaskEntity>(
    repo,
    "task", "Feladat", "Feladatok",
    "Feladatok kezelése. A feladatok javítására használd a \"Feladatok értékelése\" menüt!",
    TaskEntity::class, ::TaskEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_TASKS,
    importable = true, adminMenuIcon = "emoji_events"
)

@Controller
@RequestMapping("/admin/control/categories")
@ConditionalOnBean(TaskComponent::class)
class TaskCategoryController(
    repo: TaskCategoryRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent
) : AbstractAdminPanelController<TaskCategoryEntity>(
    repo,
    "categories", "Feladat Kategória", "Feladat kategóriák",
    "Feladatok kategóriájának kezelése. A feladatok javítására használd a Javítások menüt!",
    TaskCategoryEntity::class, ::TaskCategoryEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_TASK_CATEGORIES,
    importable = true, adminMenuPriority = 2, adminMenuIcon = "category"
)

// FIXME 1
@Controller
@RequestMapping("/admin/control/submitted-tasks")
@ConditionalOnBean(TaskComponent::class)
class TaskSubmissionsController(
    repo: SubmittedTaskRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent
) : AbstractAdminPanelController<SubmittedTaskEntity>(
    repo,
    "submitted-tasks", "Nyers beadás", "Nyers beadások",
    "Nyers feladat beadások",
    SubmittedTaskEntity::class, ::SubmittedTaskEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_CONTROL_TASKS,
    importable = false, adminMenuPriority = 5, adminMenuIcon = "raw_on",
    controlMode = CONTROL_MODE_DELETE,
    allowedToPurge = true
)
