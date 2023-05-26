package hu.bme.sch.cmsch.admin

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.annotation.PostConstruct

abstract class DashboardPage(
    private var view: String,
    private var title: String,
    private var description: String,
    private var wide: Boolean,

    private var adminMenuService: AdminMenuService,
    private var component: ComponentBase,
    private var auditLog: AuditLogService,

    private var showPermission: PermissionValidator,

    private var adminMenuCategory: String? = null,
    private var adminMenuIcon: String = "check_box_outline_blank",
    private var adminMenuPriority: Int = 1,
) {

    abstract fun getComponents(user: CmschUser): List<DashboardComponent>

    @PostConstruct
    fun init() {
        val category = adminMenuCategory ?: component.javaClass.simpleName
        adminMenuService.registerEntry(
            category, AdminMenuEntry(
                title,
                adminMenuIcon,
                "/admin/control/${view}",
                adminMenuPriority,
                showPermission
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", getComponents(user))
        model.addAttribute("user", user)

        return "dashboard"
    }

}