package hu.bme.sch.cmsch.component.script

import tools.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_PERMISSION_GROUPS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SHOW_SCRIPTS
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/script-results")
@ConditionalOnBean(ScriptComponent::class)
class ScriptResultController(
    repo: ScriptResultRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ScriptComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<ScriptResultEntity>(
    "script-results",
    ScriptResultEntity::class, ::ScriptResultEntity,
    "Script log", "Script logok",
    "Report vagy script eredménye, publikált artifactokkal",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_SCRIPTS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,
    deletePermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY,

    showEnabled = false,
    createEnabled = false,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "history",
    adminMenuPriority = 2,
    searchSettings = calculateSearchSettings<ScriptResultEntity>(false),

    controlActions = mutableListOf(
        ControlAction(
            "Logs",
            "logs/{id}",
            "menu",
            StaffPermissions.PERMISSION_SHOW_SCRIPTS,
            110,
            false,
            "Script futás megtekintése"
        ),
    )

) {

    val buttonActionsForFilteredView = buttonActions + listOf(ButtonAction(
        "Vissza",
        "back",
        PERMISSION_SHOW_SCRIPTS,
        100,
        "arrow_back",
        false
    ))

    @GetMapping("/logs/{id}")
    fun redirectView(@PathVariable id: Int): String {
        return "redirect:/admin/control/script-logs/$id"
    }

    @GetMapping("/back")
    fun redirectBack(): String {
        return "redirect:/admin/control/scripts"
    }

    @GetMapping("/{scriptId}")
    open fun viewByScriptId(model: Model, auth: Authentication, @PathVariable scriptId: Int): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", descriptor.getColumns())
        val overview = transactionManager.transaction(readOnly = true) { fetchOverview(user).filter { it.scriptId == scriptId } }
        model.addAttribute("tableData", descriptor.getTableData(filterOverview(user, overview)))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", controlActions.filter { it.permission.validate(user) })
        model.addAttribute("allControlActions", controlActions)
        model.addAttribute("buttonActions", buttonActionsForFilteredView.filter { it.permission.validate(user) })
        model.addAttribute("searchSettings", searchSettings)

        attachPermissionInfo(model)

        return "overview4"
    }

}
