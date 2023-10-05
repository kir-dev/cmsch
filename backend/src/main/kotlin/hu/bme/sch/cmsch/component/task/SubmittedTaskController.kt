package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/admin/control/submitted-tasks")
@ConditionalOnBean(TaskComponent::class)
class SubmittedTaskController(
    repo: SubmittedTaskRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    private val clock: TimeService
) : OneDeepEntityPage<SubmittedTaskEntity>(
    "submitted-tasks",
    SubmittedTaskEntity::class, ::SubmittedTaskEntity,
    "Nyers beadás", "Nyers beadások",
    "Nyers feladat beadások",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   ControlPermissions.PERMISSION_CONTROL_TASKS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   ControlPermissions.PERMISSION_CONTROL_TASKS,
    deletePermission = ControlPermissions.PERMISSION_CONTROL_TASKS,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "raw_on",
    adminMenuPriority = 5,

    buttonActions = mutableListOf(
        ButtonAction(
            name = "Id-Név export",
            "filtered-export/csv",
            StaffPermissions.PERMISSION_EDIT_GROUPS,
            300,
            icon = "export_notes",
            primary = false,
            newPage = true
        )
    ),

    searchSettings = calculateSearchSettings<SubmittedTaskEntity>(false)
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
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_EDIT_GROUPS.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-filtered-export.csv\"")
        return filterDescriptor.exportToCsv(fetchOverview(user).map {
            SubmittedTaskFilteredView(
                it.task?.id ?: 0,
                it.task?.title ?: "-",
                it.groupId ?: 0,
                it.groupName,
                it.score,
                it.approved,
                it.rejected,
                it.textAnswerLob,
                it.imageUrlAnswer,
                it.fileUrlAnswer,
                it.response
            )
        }).toByteArray()
    }


    fun saveChangeHistory(entity: SubmittedTaskEntity, userName: String) {
        entity.addSubmissionHistory(
            date = clock.getTimeInSeconds(),
            submitterName = userName,
            adminResponse = true,
            content = entity.response,
            contentUrl = "",
            status = "${entity.score} pont | ${
                if (entity.approved) "elfogadva"
                else if (entity.rejected) "elutasítva"
                else "nem értékelt"}",
            type = "TEXT"
        )
    }

    override fun onEntityPreSave(entity: SubmittedTaskEntity, auth: Authentication): Boolean {
        if (entity.approved && entity.rejected)
            entity.rejected = false
        saveChangeHistory(entity, auth.getUser().userName)
        return true
    }

}