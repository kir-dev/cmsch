package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.dto.virtual.CheckRatingVirtualEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TASKS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/check-ratings")
@ConditionalOnBean(TaskComponent::class)
class CheckRatingsAdminController(
    private val submittedRepository: SubmittedTaskRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "check-ratings"
    private val titlePlural = "Pontok ellenőrzése"
    private val permissionControl = PERMISSION_EDIT_TASKS

    private val submittedDescriptor = OverviewBuilder(CheckRatingVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            TaskComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "fact_check",
                "/admin/control/${view}",
                4,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun viewAll(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("description", "Itt azok a beadások láthatóak amik eltérnek a beadásra adható max ponttól vagy a 0 ponttól.")
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchSubmittedChecks())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    private fun fetchSubmittedChecks(): List<CheckRatingVirtualEntity> {
        return submittedRepository.findAllByScoreGreaterThanAndApprovedIsTrue(0)
            .filter { it.score != (it.task?.maxScore ?: 0) }
            .map { CheckRatingVirtualEntity(it.id, it.groupName, it.score, it.task?.maxScore ?: 0) }
    }

}
