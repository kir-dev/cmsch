package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.extrapage.ExtraPageService
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.markdownToHtml
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.annotation.PostConstruct

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
    private val extraPageService: Optional<ExtraPageService>
) {

    @PostConstruct
    fun init() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuCategory("Általános", 0))
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

        adminMenuService.registerCategory(EXPERIMENTAL_CATEGORY, AdminMenuCategory("Experimental", 1000))
    }

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    @GetMapping("/basics")
    fun dashboard(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("user", user)

        val userPermissions = user.permissions

        model.addAttribute("customPermissions", extraPageService.map { service ->
            service.getAll().groupBy { it.permissionToEdit }.map { group ->
                PermissionValidator(
                    group.key,
                    "Szükséges a(z) '${group.value.joinToString("', '") { it.title }}' " +
                            "nevű oldal(ak) szerkesztéséhez"
                )
            }
        }.orElse(listOf())
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("staffPermissions", StaffPermissions.allPermissions()
            .filter { it.permissionString.isNotEmpty() }
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("adminPermissions", ControlPermissions.allPermissions()
            .filter { it.permissionString.isNotEmpty() }
            .filter { userPermissions.contains(it.permissionString) })

        model.addAttribute("motd", applicationComponent.motd.getValue())
        model.addAttribute("staffMessage", markdownToHtml(applicationComponent.staffMessage.getValue()))

        return "admin"
    }

}
