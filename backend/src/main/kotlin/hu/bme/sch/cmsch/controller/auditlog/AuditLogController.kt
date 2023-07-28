package hu.bme.sch.cmsch.controller.auditlog

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
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
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

@Controller
@RequestMapping("/admin/control/auditlog")
class AuditLogController(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val adminMenuService: AdminMenuService,
    private val objectMapper: ObjectMapper,
    private val auditLogService: AuditLogService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val view = "auditlog"
    private val titleSingular = "Audit log"
    private val titlePlural = "Audit log"
    private val description = "Minden admin panelen végrehajtott módosítás naponta szortírozva"
    private val showPermission = ControlPermissions.PERMISSION_SHOW_AUDIT_LOG

    private val descriptor = OverviewBuilder(AuditLogVirtualEntity::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.DEVELOPER_CATEGORY, AdminMenuEntry(
                titlePlural,
                "playlist_add_check_circle",
                "/admin/control/${view}",
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

        model.addAttribute("columnData", descriptor.getColumnsAsJson())
        model.addAttribute("tableData", descriptor.getTableDataAsJson(listFilesInView()))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", descriptor.toJson(
            controlActions.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("allControlActions", controlActions)
        model.addAttribute("buttonActions", listOf<ButtonAction>())

        return "overview4"
    }

    @GetMapping("/view/{id}")
    fun viewFile(@PathVariable id: String, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, AUDIT_LOG, "GET /auditlog/view/$id", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("id", id)
        model.addAttribute("log", auditLogService.readLog(id))

        return "auditLog"
    }

    private fun listFilesInView(): List<AuditLogVirtualEntity> {
        return try {
            Files.walk(Path.of(startupPropertyConfig.auditLog))
                .asSequence()
                .filter { it.isRegularFile() }
                .sortedBy { it.fileName.toString() }
                .map { AuditLogVirtualEntity(it.fileName.toString(), "${it.fileSize() / 1024} KB") }
                .toList()
        } catch (e: IOException) {
            log.warn("No file or directory found: {}", e.message)
            listOf()
        }
    }

    @ResponseBody
    @GetMapping(value = ["/download/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun generatePdf(@PathVariable id: String, auth: Authentication, response: HttpServletResponse): ResponseEntity<ByteArray> {
        if (showPermission.validate(auth.getUser()).not()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        response.addHeader("Content-Disposition", "attachment; filename=audit-log-${id}.txt")
        return ResponseEntity.ok(auditLogService.readLog(id).toByteArray(StandardCharsets.UTF_8))
    }

}
