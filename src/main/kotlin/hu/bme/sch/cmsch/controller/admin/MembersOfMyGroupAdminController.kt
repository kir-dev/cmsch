package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.UserHandlingComponent
import hu.bme.sch.cmsch.dto.virtual.GroupMemberVirtualEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PERMISSION_IMPLICIT_HAS_GROUP
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/members-of-my-group")
class MembersOfMyGroupAdminController(
    private val userService: UserService,
    private val adminMenuService: AdminMenuService
) {

    private val view = "members-of-my-group"
    private val titlePlural = "Tanköröm tagjai"
    private val permissionControl = PERMISSION_IMPLICIT_HAS_GROUP
    private val membersDescriptor = OverviewBuilder(GroupMemberVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            UserHandlingComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "group",
                "/admin/control/${view}",
                6,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun membersOfMyGroup(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("description", "A tankörödben lévő emberek. Ameddig valaki nem jelentkezik be, addig itt nem látszik, hogy rendező-e.")
        model.addAttribute("view", view)
        model.addAttribute("columns", membersDescriptor.getColumns())
        model.addAttribute("fields", membersDescriptor.getColumnDefinitions())
        model.addAttribute("rows", userService.allMembersOfGroup(user.groupName))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

}
