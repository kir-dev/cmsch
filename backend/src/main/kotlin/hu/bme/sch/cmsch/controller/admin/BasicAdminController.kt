package hu.bme.sch.cmsch.controller.admin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.component.staticpage.StaticPageService
import hu.bme.sch.cmsch.service.AdminMenuCategory
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.markdownToHtml
import jakarta.annotation.PostConstruct
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

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
    private val staticPageService: Optional<StaticPageService>
) {

    @PostConstruct
    fun init() {
        adminMenuService.registerCategory(javaClass.simpleName, AdminMenuCategory("Általános", 0))
        adminMenuService.registerEntry(
            javaClass.simpleName, AdminMenuEntry(
                "Rendezői oldal",
                "home",
                "/admin/control/basics",
                1,
                PERMISSION_IMPLICIT_ANYONE
            )
        )
        adminMenuService.registerEntry(
            javaClass.simpleName, AdminMenuEntry(
                "Oldal megnyitása",
                "launch",
                "/control/open-site",
                2,
                PERMISSION_IMPLICIT_ANYONE
            )
        )
    }

    @GetMapping("")
    fun index(): String {
        return "redirect:/admin/control/basics"
    }

    private val docsReader = jacksonObjectMapper().readerFor(object : TypeReference<List<DocsForOrganizers>>() {})

    @GetMapping("/basics")
    fun dashboard(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("user", user)

        model.addAttribute("staffMessage", markdownToHtml(applicationComponent.staffMessage.getValue()))
        model.addAttribute("docs", docsReader
            .readValue<List<DocsForOrganizers>>(applicationComponent.documentsForOrganizers.getValue())
            .filter { it.visible })

        return "admin"
    }

}
