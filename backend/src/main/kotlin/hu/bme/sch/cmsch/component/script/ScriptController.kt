package hu.bme.sch.cmsch.component.script

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/scripts")
@ConditionalOnBean(ScriptComponent::class)
class ScriptController(
    repo: ScriptRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ScriptComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<ScriptEntity>(
    "scripts",
    ScriptEntity::class, ::ScriptEntity,
    "Script", "Scriptek",
    "Report generáló vagy adatbázist módosító Kotlin nyelven írt scriptek. Az összes tábla elérhető vele " +
            "az adatbázisból.",

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
    createPermission = StaffPermissions.PERMISSION_CREATE_SCRIPTS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_SCRIPTS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_SCRIPTS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "code_blocks",
    adminMenuPriority = 1,
    searchSettings = calculateSearchSettings<ScriptEntity>(false),

    controlActions = mutableListOf(
        ControlAction(
            "Execute",
            "execute/{id}",
            "play_arrow",
            StaffPermissions.PERMISSION_EXECUTE_SCRIPTS,
            110,
            false,
            "Script futtatása"
        ),
        ControlAction(
            "Results",
            "results/{id}",
            "history",
            StaffPermissions.PERMISSION_SHOW_SCRIPTS,
            120,
            false,
            "Script logok"
        ),
    )

) {

    @GetMapping("/execute/{id}")
    fun redirectExecute(@PathVariable id: Int): String {
        return "redirect:/admin/control/script-execute/$id"
    }

    @GetMapping("/results/{id}")
    fun redirectResults(@PathVariable id: Int): String {
        return "redirect:/admin/control/script-results/$id"
    }

}
