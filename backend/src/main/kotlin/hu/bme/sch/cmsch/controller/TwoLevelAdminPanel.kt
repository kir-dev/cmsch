package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_IMPORT_EXPORT
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.util.getUser
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.annotation.PostConstruct
import kotlin.reflect.KClass

abstract class TwoLevelAdminPanel<T1 : ManagedEntity, T2 : Any>(
    internal val view: String,
    private val titleSingular: String,
    private val titlePlural: String,
    private val description: String,
    overviewClass: KClass<T1>,
    sublistClass: KClass<T2>,
    private val adminMenuService: AdminMenuService,
    private val component: ComponentBase,
    internal val permissionControl: PermissionValidator,
    private val adminMenuIcon: String = "check_box_outline_blank",
    private val adminMenuPriority: Int = 1,
    internal val savable: Boolean = false,
) {

    private val overviewDescriptor = OverviewBuilder(overviewClass)
    internal val sublistDescriptor = OverviewBuilder(sublistClass)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(component.javaClass.simpleName, AdminMenuEntry(
            titlePlural,
            adminMenuIcon,
            "/admin/control/${view}",
            adminMenuPriority,
            permissionControl
        ))
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
        model.addAttribute("rows", filterOverview(user, fetchOverview()))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)
        model.addAttribute("importable", false)
        model.addAttribute("allowedToPurge", false)
        model.addAttribute("savable", false)
        model.addAttribute("savableSublist", false)
        model.addAttribute("filteredExport", false)

        return "overview"
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

        model.addAttribute("id", id)
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", sublistDescriptor.getColumns())
        model.addAttribute("fields", sublistDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchSublist(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)
        model.addAttribute("importable", false)
        model.addAttribute("allowedToPurge", false)
        model.addAttribute("savable", false)
        model.addAttribute("savableSublist", savable && PERMISSION_IMPORT_EXPORT.validate(user))
        model.addAttribute("filteredExport", false)

        return "overview"
    }

    abstract fun fetchOverview(): Iterable<T1>

    open fun filterOverview(user: CmschUser, rows: Iterable<T1>): Iterable<T1> {
        return rows
    }

    abstract fun fetchSublist(id: Int): Iterable<T2>

}
