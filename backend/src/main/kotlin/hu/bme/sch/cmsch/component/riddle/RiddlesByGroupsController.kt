package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_IMPORT_EXPORT
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin/control/riddles-by-groups")
@ConditionalOnBean(RiddleComponent::class)
class RiddlesByGroupsController(
    private val riddleMappingRepository: RiddleMappingRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "riddles-by-groups"
    private val titleSingular = "Riddle beadás csoportonként"
    private val titlePlural = "Riddle csapatonként"
    private val description = "Beadott riddleök csoportonként csoportosítva"
    private val permissionControl = PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS

    private val overviewDescriptor = OverviewBuilder(RiddleStatsVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(RiddleMappingVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            RiddleComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "checklist_rtl",
                "/admin/control/${view}",
                4,
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
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)
        model.addAttribute("filteredExport", PERMISSION_IMPORT_EXPORT.validate(user))

        return "overview"
    }

    private fun fetchOverview(): List<RiddleStatsVirtualEntity> {
        return riddleMappingRepository.findAll().groupBy { it.ownerGroup?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    RiddleStatsVirtualEntity(
                            submissions[0].ownerGroup?.id ?: 0,
                            submissions[0].ownerGroup?.name ?: "n/a",
                            submissions.count { it.completed },
                            submissions.count { it.hintUsed }
                    )
                }
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
        model.addAttribute("columns", propertyDescriptor.getColumns())
        model.addAttribute("fields", propertyDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchProperties(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_DELETE)

        return "overview"
    }

    private fun fetchProperties(user: Int): List<RiddleMappingVirtualEntity> {
        return riddleMappingRepository.findAllByOwnerGroup_Id(user)
            .map { submission ->
                RiddleMappingVirtualEntity(
                    submission.id,
                    submission.riddle?.categoryId ?: 0,
                    submission.riddle?.title ?: "n/a",
                    submission.hintUsed,
                    submission.completed,
                    submission.attemptCount,
                    submission.completedAt
                )
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

        val entity = riddleMappingRepository.findById(id)
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

        val entity = riddleMappingRepository.findById(id).orElseThrow()
        riddleMappingRepository.delete(entity)
        return "redirect:/admin/control/$view/"
    }

    data class RiddleByGroupFilteredView(
        @property:ImportFormat(ignore = false, columnId = 0, type = IMPORT_INT)
        var riddleId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 1)
        var riddleName: String = "",

        @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
        var groupId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 3)
        var groupName: String = "",

        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
        var score: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_BOOLEAN)
        var hint: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_BOOLEAN)
        var completed: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_INT)
        var attemptCount: Int = 0,
    )

    private val filterDescriptor = OverviewBuilder(RiddleByGroupFilteredView::class)

    @ResponseBody
    @GetMapping("/filtered-export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun filteredExport(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-filtered-export.csv\"")
        return filterDescriptor.exportToCsv(riddleMappingRepository.findAll()
            .filter { it.completed }
            .map {
            RiddleByGroupFilteredView(
                it.riddle?.id ?: 0,
                it.riddle?.title ?: "-",
                it.ownerGroup?.id ?: 0,
                it.ownerGroup?.name ?: "",
                it.riddle?.score ?: 0,
                it.hintUsed,
                it.completed,
                it.attemptCount
            )
        }).toByteArray()
    }

}