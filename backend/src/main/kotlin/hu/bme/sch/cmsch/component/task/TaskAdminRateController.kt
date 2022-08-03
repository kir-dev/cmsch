package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.admin.INPUT_TYPE_FILE
import hu.bme.sch.cmsch.admin.INTERPRETER_INHERIT
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_RATE_TASKS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import kotlin.reflect.KMutableProperty1

const val CONTROL_MODE_RATE = "rate"
const val CONTROL_MODE_GRADE = "grade"

@Controller
@RequestMapping("/admin/control/rate-tasks")
@ConditionalOnBean(TaskComponent::class)
class TaskAdminRateController(
    private val submittedRepository: SubmittedTaskRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "rate-tasks"
    private val titleSingular = "Értékelés"
    private val titlePlural = "Értékelések"
    private val description = "A beadott feladatok értékelése"
    private val permissionControl = PERMISSION_RATE_TASKS

    private val entitySourceMapping: Map<String, (SubmittedTaskEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val overviewDescriptor = OverviewBuilder(GradedTaskGroupDto::class)
    private val submittedDescriptor = OverviewBuilder(SubmittedTaskEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            TaskComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "thumbs_up_down",
                "/admin/control/${view}",
                3,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_RATE)

        return "overview"
    }

    private fun fetchOverview(): List<GradedTaskGroupDto> {
        return submittedRepository.findAll().asSequence()
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

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", submittedRepository.findByTask_Id(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    @GetMapping("/rate/{id}")
    fun rate(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", submittedRepository.findByTask_IdAndRejectedIsFalseAndApprovedIsFalse(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    @GetMapping("/grade/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", submittedDescriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
            model.addAttribute("taskTitle", entity.orElseThrow().task?.title)
            model.addAttribute("taskDescription", entity.orElseThrow().task?.description)
            val maxScore = entity.orElseThrow().task?.maxScore ?: 0
            model.addAttribute("comment", "Feladványhoz tartozó max pont: $maxScore")
            model.addAttribute("maxScore", maxScore)
        }
        return "details"
    }

    @PostMapping("/grade/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: SubmittedTaskEntity,
             model: Model,
             auth: Authentication
    ): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/grade/$id"
        }

        updateEntity(submittedDescriptor, entity.get(), dto)
        if (entity.get().approved && entity.get().rejected)
            entity.get().rejected = false
        entity.get().id = id
        submittedRepository.save(entity.get())
        return "redirect:/admin/control/$view/rate/${entity.get().task?.id ?: ""}"
    }

    private fun updateEntity(
        descriptor: OverviewBuilder,
        entity: SubmittedTaskEntity,
        dto: SubmittedTaskEntity
    ) {
        descriptor.getInputs().forEach {
            if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore) {
                when {
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type != INPUT_TYPE_FILE -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto))
                    }
                }
            }
        }
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", user)

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("item", entity.orElseThrow().toString())
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = submittedRepository.findById(id).orElseThrow()
        submittedRepository.delete(entity)
        return "redirect:/admin/control/$view/"
    }


}
