package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuGroup
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE
import hu.bme.sch.cmsch.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_TOPLIST = "toplist"
const val CONTROL_MODE_PAYED = "payed"
const val CONTROL_MODE_NONE = "none"
const val CONTROL_MODE_TRACK = "track"
const val CONTROL_MODE_PDF = "pdf"

const val EXPERIMENTAL_CATEGORY = "EXPERIMENTAL_CATEGORY"

@Controller
@RequestMapping("/admin/control")
class BasicAdminController(
    private val applicationComponent: ApplicationComponent,
    private val adminMenuService: AdminMenuService,
) {

    @PostConstruct
    fun init() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuGroup("Általános", 0))
        adminMenuService.registerEntry(
            javaClass.simpleName, AdminMenuEntry(
                "Kezdő menü",
                "home",
                "/admin/control/basics",
                1,
                PERMISSION_IMPLICIT_ANYONE
            )
        )
        adminMenuService.registerEntry(
            javaClass.simpleName, AdminMenuEntry(
                "Oldal megyitása",
                "launch",
                "/control/open-site",
                2,
                PERMISSION_IMPLICIT_ANYONE
            )
        )

        adminMenuService.registerCategory(EXPERIMENTAL_CATEGORY, AdminMenuGroup("Experimental", 1000))
    }

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    @GetMapping("/basics")
    fun dashboard(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("user", user)
        model.addAttribute("motd", applicationComponent.motd.getValue())
        model.addAttribute("website", applicationComponent.siteUrl.getValue())
        model.addAttribute("staffMessage", applicationComponent.staffMessage.getValue())

        return "admin"
    }

}
