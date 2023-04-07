package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.form.*
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.INVALID_ID_ERROR
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.markdownToHtml
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/control/rate-tasks")
@ConditionalOnBean(TaskComponent::class)
class TaskAdminRateController(
    private val submittedRepository: SubmittedTaskRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : TwoDeepEntityPage<GradedTaskGroupDto, SubmittedTaskEntity>(
    "rate-tasks",
    GradedTaskGroupDto::class,
    SubmittedTaskEntity::class, ::SubmittedTaskEntity,
    "Értékelés", "Értékelések",
    "A beadott feladatok értékelése",

    object : ManualRepository<GradedTaskGroupDto, Int>() {
        override fun findAll(): Iterable<GradedTaskGroupDto> {
            return submittedRepository.findAll()
                .groupBy { it.task }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    GradedTaskGroupDto(
                        submissions[0].task?.id ?: 0,
                        submissions[0].task?.title ?: "n/a",
                        submissions.count { it.approved },
                        submissions.count { it.rejected },
                        submissions.count { !it.approved && !it.rejected }
                    )
                }
                .sortedByDescending { it.notGraded }.toList()
        }

    },
    submittedRepository,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_RATE_TASKS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   StaffPermissions.PERMISSION_RATE_TASKS,
    deletePermission = StaffPermissions.PERMISSION_RATE_TASKS,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "thumbs_up_down",
    adminMenuPriority = 3,

    outerControlActions = mutableListOf(
        ControlAction(
            "Értékel",
            "rate/{id}",
            "thumbs_up_down",
            StaffPermissions.PERMISSION_RATE_TASKS,
            200,
            false
        )
    )
) {

    private val rateControlActions = mutableListOf(
        ControlAction(
            "Kijavít",
            "grade/{id}",
            "grade",
            StaffPermissions.PERMISSION_RATE_TASKS,
            100,
            false
        )
    )

    override fun fetchSublist(id: Int): Iterable<SubmittedTaskEntity> {
        return submittedRepository.findByTask_Id(id)
    }

    @GetMapping("/rate/{id}")
    fun rate(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (viewPermission.validate(user).not()) {
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", descriptor.getColumnsAsJson())
        model.addAttribute("tableData", descriptor.getTableDataAsJson(
            submittedRepository.findByTask_IdAndRejectedIsFalseAndApprovedIsFalse(id)))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", descriptor.toJson(
            rateControlActions.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("buttonActions", buttonActions.filter { it.permission.validate(user) })

        return "overview4"
    }

    @GetMapping("/grade/{id}")
    fun grade(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("gradeMode", true)
        model.addAttribute("readOnly", false)
        // FIXME: set edit path to grade

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
            model.addAttribute("taskTitle", entity.orElseThrow().task?.title)
            model.addAttribute("taskDescription", entity.orElseThrow().task?.description?.let { markdownToHtml(it) })
            val maxScore = entity.orElseThrow().task?.maxScore ?: 0
            model.addAttribute("comment", "Feladványhoz tartozó max pont: $maxScore")
            model.addAttribute("maxScore", maxScore)
        }
        return "details"
    }

    @PostMapping("/grade/{id}")
    fun grade(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: SubmittedTaskEntity,
             model: Model,
             auth: Authentication
    ): String {
        val user = auth.getUser()
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/grade/$id"
        }

        updateEntity(descriptor, user, entity.get(), dto, null, null)
        if (entity.get().approved && entity.get().rejected)
            entity.get().rejected = false
        entity.get().id = id
        submittedRepository.save(entity.get())
        return "redirect:/admin/control/$view/rate/${entity.get().task?.id ?: ""}"
    }

    override fun onEntityPreSave(entity: SubmittedTaskEntity, auth: Authentication): Boolean {
        if (entity.approved && entity.rejected)
            entity.rejected = false
        return true
    }

}
