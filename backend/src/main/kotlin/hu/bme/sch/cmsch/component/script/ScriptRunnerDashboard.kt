package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPage
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserEntityFromDatabase
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import kotlin.jvm.optionals.getOrNull

@Controller
@RequestMapping("/admin/control/script-execute")
@ConditionalOnBean(ScriptComponent::class)
class ScriptRunnerDashboard(
    private val adminMenuService: AdminMenuService,
    scriptComponent: ScriptComponent,
    private val auditLogService: AuditLogService,
    private val userRepository: UserRepository,
    private val scriptRepository: ScriptRepository,
    private val scriptService: ScriptService,
    private val platformTransactionManager: PlatformTransactionManager,
    private val userService: UserService
) : DashboardPage(
    "script-execute",
    "Script futtatása",
    "",
    false,
    adminMenuService,
    scriptComponent,
    auditLogService,
    StaffPermissions.PERMISSION_EXECUTE_SCRIPTS,
    adminMenuCategory = null,
    ignoreFromMenu = true
) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf()
    }

    @GetMapping("/{scriptId}")
    fun viewScript(model: Model, @PathVariable scriptId: Int, auth: Authentication, @RequestParam requestParams: Map<String, String>): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            return "admin403"
        }

        val scriptEntity = platformTransactionManager.transaction(readOnly = true) {
            scriptRepository.findById(scriptId).getOrNull()
                ?: throw IllegalStateException("No script found")
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", listOf(getExecuteCard(scriptEntity)))
        model.addAttribute("user", user)
        model.addAttribute("card", requestParams.getOrDefault("card", "-1").toIntOrNull())
        model.addAttribute("message", requestParams.getOrDefault("message", ""))

        return "dashboard"
    }

    fun getExecuteCard(script: ScriptEntity): DashboardFormCard {
        val content = mutableListOf(
            FormElement(
                fieldName = "_name", label = "Script neve", type = FormElementType.INFO_BOX,
                formatRegex = ".*", invalidFormatMessage = "",
                values = "",
                note = script.name,
                required = true, permanent = false,
                defaultValue = ""
            ),
            FormElement(
                fieldName = "_description", label = "Leírás", type = FormElementType.INFO_BOX,
                formatRegex = ".*", invalidFormatMessage = "",
                values = "",
                note = script.description,
                required = true, permanent = false,
                defaultValue = ""
            ),
        )

        if (!script.entities.isEmpty()) {
            if (script.entities.trim() == "*") {
                content.add(FormElement(
                    fieldName = "_entities", label = "Használt adatbázis entitások", type = FormElementType.WARNING_BOX,
                    formatRegex = ".*", invalidFormatMessage = "",
                    values = "Az összes",
                    note = "",
                    required = true, permanent = false,
                    defaultValue = ""
                ))
            } else {
                content.add(FormElement(
                    fieldName = "_entities", label = "Használt adatbázis entitások", type = FormElementType.INFO_BOX,
                    formatRegex = ".*", invalidFormatMessage = "",
                    values = "",
                    note = script.entities,
                    required = true, permanent = false,
                    defaultValue = ""
                ))
            }
        }

        if (!script.readOnly) {
            content.add(FormElement(
                fieldName = "_notReadOnly", label = "FIGYELEM", type = FormElementType.WARNING_BOX,
                formatRegex = ".*", invalidFormatMessage = "",
                values = "Ez a script képes az adatbázis és beállítások módosítására!",
                note = "",
                required = true, permanent = false,
                defaultValue = ""
            ))
        }

        return DashboardFormCard(
            2,
            false,
            "Script futtatása",
            "",
            content,
            buttonCaption = if (script.readOnly) "Futtatás" else "Futtatás (módosító)",
            buttonIcon = "play_arrow",
            action = "${script.id}/execute",
            method = "post"
        )
    }

    @PostMapping("/{scriptId}/execute")
    fun sendTemplate(auth: Authentication, @PathVariable scriptId: Int, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUserEntityFromDatabase(userService)
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val scriptEntity = platformTransactionManager.transaction(readOnly = true) {
            scriptRepository.findById(scriptId).getOrNull()
                ?: throw IllegalStateException("No script found")
        }

        val logMessage = "User executing script $scriptId: ${scriptEntity.name}"
        auditLogService.fine(user, "script", logMessage)
        log.info(logMessage)

        val result = scriptService.executeScript(user, scriptEntity)

        return "redirect:/admin/control/script-logs/${result.first}"
    }

}
