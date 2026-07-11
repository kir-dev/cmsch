package hu.bme.sch.cmsch.component.support

import tools.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/control/support")
@ConditionalOnBean(SupportComponent::class)
class SupportAdminController(
    private val threadRepo: SupportThreadRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    private val supportComponent: SupportComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService,
    private val supportService: SupportService,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val viewExtensions: List<SupportThreadViewExtension> = emptyList()
) : OneDeepEntityPage<SupportThreadEntity>(
    "support",
    SupportThreadEntity::class, ::SupportThreadEntity,
    "Megkeresés", "Ügyfélszolgálat",
    "A beérkező ügyfélszolgálati kérések kezelése.",

    transactionManager,
    threadRepo,
    importService,
    adminMenuService,
    storageService,
    supportComponent,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS,
    deletePermission = ImplicitPermissions.PERMISSION_NOBODY,

    showEnabled   = false,
    createEnabled = false,
    editEnabled   = false,
    deleteEnabled = false,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "support_agent",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<SupportThreadEntity>(false),

    controlActions = mutableListOf(
        ControlAction(
            "Megtekint / Válaszol",
            "view/{id}",
            "forum",
            StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS,
            100,
            false,
            "Szál megtekintése és megválaszolása"
        )
    )
) {

    @GetMapping("/view/{id}")
    fun viewThread(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /support/view/$id", showPermission.permissionString)
            return "admin403"
        }

        val thread = transactionManager.transaction(readOnly = true) { threadRepo.findById(id) }
            .orElse(null)
            ?: return "redirect:/admin/control/support"

        val messages = supportService.getMessagesForThread(thread.uuid)

        val threadUser = if (thread.userInternalId.isNotBlank())
            transactionManager.transaction(readOnly = true) { userRepository.findByInternalId(thread.userInternalId) }.orElse(null)
        else null
        val roleName = threadUser?.let { resolveRoleName(it.role.name, supportComponent.roleMappings) }
        val adminEntity = transactionManager.transaction(readOnly = true) { userRepository.findByInternalId(user.internalId) }.orElse(null)
        val defaultName = adminEntity?.let { userService.resolveConfig(it.config).supportDefaultName } ?: ""

        val extensionButtons = viewExtensions.flatMap { it.getButtons(thread, user) }

        model.addAttribute("thread", thread)
        model.addAttribute("messages", messages)
        model.addAttribute("user", user)
        model.addAttribute("currentAdminName", user.userName)
        model.addAttribute("canAnswer", StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.validate(user))
        model.addAttribute("waitingTime", computeWaitingTime(thread))
        model.addAttribute("view", view)
        model.addAttribute("threadUserRole", roleName)
        model.addAttribute("extensionButtons", extensionButtons)
        model.addAttribute("defaultSolverName", defaultName.ifBlank { thread.solver.ifBlank { user.userName } })
        return "supportThreadView"
    }

    private fun resolveRoleName(roleName: String, mappings: String): String {
        if (mappings.isBlank()) return roleName
        return mappings.split(",").mapNotNull { entry ->
            val parts = entry.trim().split("=", limit = 2)
            if (parts.size == 2 && parts[0].trim().equals(roleName, ignoreCase = true)) parts[1].trim() else null
        }.firstOrNull() ?: roleName
    }

    @PostMapping("/settings/default-name")
    fun saveDefaultName(
        @RequestParam(defaultValue = "") supportDefaultName: String,
        auth: Authentication
    ): String {
        val user = auth.getUser()
        val entity = userRepository.findByInternalId(user.internalId).orElse(null)
            ?: return "redirect:/admin/control/settings"
        val config = userService.resolveConfig(entity.config)
        config.supportDefaultName = supportDefaultName.trim()
        userService.saveUserConfig(user, config)
        return "redirect:/admin/control/settings"
    }

    private fun computeWaitingTime(thread: SupportThreadEntity): String {
        if (thread.status == SupportThreadStatus.DONE) return "–"
        val referenceTime = if (thread.lastCustomerAnswerAt > 0) thread.lastCustomerAnswerAt else thread.createdAt
        val elapsedSeconds = (System.currentTimeMillis() / 1000) - referenceTime
        if (elapsedSeconds < 0) return "–"
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        return "%d:%02d".format(hours, minutes)
    }

    @PostMapping("/reply/{id}")
    fun replyToThread(
        @PathVariable id: Int,
        @RequestParam content: String,
        @RequestParam(defaultValue = "") solver: String,
        @RequestParam(defaultValue = "false") internalOnly: Boolean,
        auth: Authentication
    ): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.validate(user).not()) {
            auditLog.admin403(user, component.component, "POST /support/reply/$id", StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.permissionString)
            return "redirect:/admin/control/support"
        }
        if (content.isNotBlank()) {
            val displayName = solver.ifBlank { user.userName }
            supportService.addAdminReply(id, content, user, displayName, internalOnly)
            val type = if (internalOnly) "internal comment" else "reply"
            auditLog.edit(user, component.component, "Admin $type on support thread $id")
        }
        return "redirect:/admin/control/support/view/$id"
    }

    @PostMapping("/claim/{id}")
    fun claimThread(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.validate(user).not()) {
            auditLog.admin403(user, component.component, "POST /support/claim/$id", StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.permissionString)
            return "redirect:/admin/control/support"
        }
        val adminEntity = userRepository.findByInternalId(user.internalId).orElse(null)
        val defaultName = adminEntity?.let { userService.resolveConfig(it.config).supportDefaultName } ?: ""
        supportService.claimThread(id, user, defaultName)
        auditLog.edit(user, component.component, "Admin claimed support thread $id")
        return "redirect:/admin/control/support/view/$id"
    }

    @PostMapping("/close/{id}")
    fun closeThread(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.validate(user).not()) {
            auditLog.admin403(user, component.component, "POST /support/close/$id", StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.permissionString)
            return "redirect:/admin/control/support"
        }
        supportService.closeThread(id)
        auditLog.edit(user, component.component, "Admin closed support thread $id")
        return "redirect:/admin/control/support/view/$id"
    }

    @PostMapping("/reopen/{id}")
    fun reopenThread(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.validate(user).not()) {
            auditLog.admin403(user, component.component, "POST /support/reopen/$id", StaffPermissions.PERMISSION_ANSWER_SUPPORT_THREADS.permissionString)
            return "redirect:/admin/control/support"
        }
        supportService.reopenThread(id)
        auditLog.edit(user, component.component, "Admin reopened support thread $id")
        return "redirect:/admin/control/support/view/$id"
    }
}
