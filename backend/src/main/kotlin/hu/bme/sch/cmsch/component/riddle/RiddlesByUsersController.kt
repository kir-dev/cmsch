package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/riddles-by-users")
@ConditionalOnBean(RiddleComponent::class)
class RiddlesByUsersController(
    private val riddleMappingRepository: RiddleMappingRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "riddles-by-users"
    private val titleSingular = "Riddle beadás felhasználónként"
    private val titlePlural = "Riddle felasználónként"
    private val description = "Beadott riddleök felhasználónként csoportosítva"
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
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<RiddleStatsVirtualEntity> {
        return riddleMappingRepository.findAll().groupBy { it.ownerUser?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    RiddleStatsVirtualEntity(
                            submissions[0].ownerUser?.id ?: 0,
                            submissions[0].ownerUser?.fullName ?: "n/a",
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
        return riddleMappingRepository.findAllByOwnerUser_Id(user)
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

}
