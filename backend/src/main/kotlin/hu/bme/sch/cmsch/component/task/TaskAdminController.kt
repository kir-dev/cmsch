package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TASKS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TASKS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TASK_CATEGORIES
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

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
    allowedToPurge = true,
    filteredExport = true
) {

    data class SubmittedTaskFilteredView(
        @property:ImportFormat(ignore = false, columnId = 0, type = IMPORT_INT)
        var taskId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 1)
        var taskName: String = "",

        @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
        var groupId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 3)
        var groupName: String = "",

        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
        var score: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_BOOLEAN)
        var approved: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_BOOLEAN)
        var rejected: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_LOB)
        var textAnswer: String = "",

        @property:ImportFormat(ignore = false, columnId = 8)
        var imageAnswer: String = "",

        @property:ImportFormat(ignore = false, columnId = 9)
        var fileAnswer: String = "",

        @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LOB)
        var response: String = "",
    )

    private val filterDescriptor = OverviewBuilder(SubmittedTaskFilteredView::class)

    @ResponseBody
    @GetMapping("/filtered-export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun filteredExport(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (ControlPermissions.PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-filtered-export.csv\"")
        return filterDescriptor.exportToCsv(fetchOverview().map {
            SubmittedTaskFilteredView(
                it.task?.id ?: 0,
                it.task?.title ?: "-",
                it.groupId ?: 0,
                it.groupName,
                it.score,
                it.approved,
                it.rejected,
                it.textAnswer,
                it.imageUrlAnswer,
                it.fileUrlAnswer,
                it.response
            )
        }).toByteArray()
    }

}
