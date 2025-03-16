package hu.bme.sch.cmsch.controller.auditlog

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.model.AuditLogByDayEntry
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/admin/control/auditlog")
class AuditLogController(
    private val adminMenuService: AdminMenuService,
    private val auditLogService: AuditLogService
) {

    private val view = "auditlog"
    private val titleSingular = "Audit log"
    private val titlePlural = "Audit log"
    private val description = "Minden admin panelen végrehajtott módosítás naponta szortírozva"
    private val showPermission = ControlPermissions.PERMISSION_SHOW_AUDIT_LOG

    private val descriptor = OverviewBuilder(AuditLogByDayEntry::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.DEVELOPER_CATEGORY, AdminMenuEntry(
                titlePlural,
                "playlist_add_check_circle",
                "/admin/control/$view",
                20,
                showPermission
            )
        )

        controlActions.add(
            ControlAction(
                "Megnyitás",
                "view/{id}",
                "double_arrow",
                showPermission,
                100,
                usageString = "Audit log megnyitása",
                basic = true
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
            auditLogService.admin403(user, AUDIT_LOG, "GET /auditlog", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("exportEnabled", true)

        model.addAttribute("columnData", descriptor.getColumns())
        model.addAttribute("tableData", descriptor.getTableData(auditLogService.listDaysWithLogs()))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", controlActions.filter { it.permission.validate(user) })
        model.addAttribute("allControlActions", controlActions)

        val exportAction = ButtonAction("Export", "export", showPermission, 200, "upload_file", false)
        model.addAttribute("buttonActions", listOf<ButtonAction>(exportAction))

        return "overview4"
    }

    @GetMapping("/view/{id}")
    fun viewFile(@PathVariable id: Long, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, AUDIT_LOG, "GET /auditlog/view/$id", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("id", id)
        model.addAttribute("log", auditLogService.getLogTextForDay(id))

        return "auditLog"
    }

    @ResponseBody
    @GetMapping("/export", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportAll(auth: Authentication, response: HttpServletResponse): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"audit-log-full.txt\"")
        return auditLogService.getAllLogText()
    }

    @ResponseBody
    @GetMapping(value = ["/download/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generatePdf(
        @PathVariable id: Long,
        auth: Authentication,
        response: HttpServletResponse
    ): ResponseEntity<ByteArray> {
        if (showPermission.validate(auth.getUser()).not()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        response.addHeader("Content-Disposition", "attachment; filename=audit-log-${id}.txt")
        return ResponseEntity.ok(auditLogService.getLogTextForDay(id).toByteArray(StandardCharsets.UTF_8))
    }

}
