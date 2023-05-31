package hu.bme.sch.cmsch.controller.admin

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.virtual.FileVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.FilesByViewVirtualEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import jakarta.annotation.PostConstruct
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

@Controller
@RequestMapping("/admin/control/files")
class FilesByViewController(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val adminMenuService: AdminMenuService,
    private val objectMapper: ObjectMapper,
    private val auditLog: AuditLogService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val view = "files"
    private val titleSingular = "Fájl"
    private val titlePlural = "Fájlok"
    private val description = "Fájlok kategóriánként csoportosítva"
    private val showPermission = ControlPermissions.PERMISSION_SHOW_FILES
    private val deletePermission = ControlPermissions.PERMISSION_DELETE_FILES

    private val overviewDescriptor = OverviewBuilder(FilesByViewVirtualEntity::class)
    private val submittedDescriptor = OverviewBuilder(FileVirtualEntity::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.CONTENT_CATEGORY, AdminMenuEntry(
                titlePlural,
                "folder",
                "/admin/control/${view}",
                1,
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
                usageString = "Kategória megnyitása",
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
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", overviewDescriptor.getColumnsAsJson())
        model.addAttribute("tableData", overviewDescriptor.getTableDataAsJson(fetchOverview()))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", overviewDescriptor.toJson(
            controlActions.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("allControlActions", controlActions)
        model.addAttribute("buttonActions", listOf<ButtonAction>())

        return "overview4"
    }

    private fun fetchOverview(): List<FilesByViewVirtualEntity> {
        return sequenceOf("profiles", "news", "events", "products", "groups", "task", "public")
                .filter { Files.exists(Path.of(startupPropertyConfig.external, it)) }
                .map { FilesByViewVirtualEntity(it, Files.walk(Path.of(startupPropertyConfig.external, it)).count() - 1, "~${Path.of(startupPropertyConfig.external, it).fileSize() / 1024} KB") }
                .toList()

    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: String, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", submittedDescriptor.getColumnsAsJson())
        model.addAttribute("tableData", submittedDescriptor.getTableDataAsJson(listFilesInView(id)))

        model.addAttribute("user", user)
        val controlActionForCategory = listOf(
            ControlAction(
                "Megnyitás",
                "cdn/${id}/{id}",
                "visibility",
                showPermission,
                100,
                true,
                usageString = "File megnyitása",
                basic = true
            ),
            ControlAction(
                "Törlés",
                "delete/${id}/{id}",
                "delete",
                deletePermission,
                200,
                false,
                usageString = "File törlése",
                basic = true
            )
        )
        model.addAttribute("controlActions", overviewDescriptor.toJson(
            controlActionForCategory.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("allControlActions", controlActionForCategory)
        model.addAttribute("buttonActions", listOf<ButtonAction>())

        return "overview4"
    }

    private fun listFilesInView(view: String): List<FileVirtualEntity> {
        return try {
            Files.walk(Path.of(startupPropertyConfig.external, view))
                .asSequence()
                .filter { it.isRegularFile() }
                .sortedByDescending { it.fileSize() }
                .map { FileVirtualEntity(it.fileName.toString(), "${it.fileSize() / 1024} KB") }
                .toList()
        } catch (e: IOException) {
            log.warn("No file or directory found: {}", e.message)
            listOf()
        }
    }

    @GetMapping("/cdn/{type}/{id}")
    fun redirectCdn(@PathVariable type: String, @PathVariable id: String): String {
        return "redirect:/cdn/${type}/${id}"
    }

    @GetMapping("/delete/{type}/{id}")
    fun deleteConfirm(@PathVariable type: String, @PathVariable id: String, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", "$type/$id")
        model.addAttribute("user", user)
        model.addAttribute("item", id)

        return "delete"
    }

    @PostMapping("/delete/{type}/{id}")
    fun delete(@PathVariable type: String, @PathVariable id: String, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        auditLog.delete(user, "files", Path.of(startupPropertyConfig.external, type, id).toString())
        Files.deleteIfExists(Path.of(startupPropertyConfig.external, type, id))

        return "redirect:/admin/control/$view/view/$type"
    }

}
