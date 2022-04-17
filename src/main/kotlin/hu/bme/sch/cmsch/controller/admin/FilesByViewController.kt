package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW
import hu.bme.sch.cmsch.dto.virtual.FileVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.FilesByViewVirtualEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.PERMISSION_SHOW_DELETE_FILES
import hu.bme.sch.cmsch.util.getUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Path
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

const val CONTROL_MODE_FILE = "file"

@Controller
@RequestMapping("/admin/control/files")
class FilesByViewController(
        @Value("\${cmsch.external:/etc/cmsch/external/}") private val root: String,
        private val clock: ClockService,
        private val adminMenuService: AdminMenuService
) {

    private val view = "files"
    private val titleSingular = "Fájl"
    private val titlePlural = "Fájlok"
    private val description = "Fájlok kategóriánként csoportosítva"
    private val permissionControl = PERMISSION_SHOW_DELETE_FILES

    private val overviewDescriptor = OverviewBuilder(FilesByViewVirtualEntity::class)
    private val submittedDescriptor = OverviewBuilder(FileVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "folder",
                "/admin/control/${view}",
                10,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
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
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<FilesByViewVirtualEntity> {
        return sequenceOf("profiles", "news", "events", "products", "groups", "achievement")
                .filter { Files.exists(Path.of(root, it)) }
                .map { FilesByViewVirtualEntity(it, Files.walk(Path.of(root, it)).count() - 1, "~${Path.of(root, it).fileSize() / 1024} KB") }
                .toList()

    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: String, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
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
        model.addAttribute("id", id)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", listFilesInView(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_FILE)

        return "overview"
    }

    private fun listFilesInView(view: String): List<FileVirtualEntity> {
        return Files.walk(Path.of(root, view))
                .asSequence()
                .filter { it.isRegularFile() }
                .sortedByDescending { it.fileSize() }
                .map { FileVirtualEntity(it.fileName.toString(), "${it.fileSize() / 1024} KB") }
                .toList()
    }

    @GetMapping("/delete/{type}/{id}")
    fun deleteConfirm(@PathVariable type: String, @PathVariable id: String, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
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
    fun delete(@PathVariable type: String, @PathVariable id: String, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        Files.deleteIfExists(Path.of(root, type, id))

        return "redirect:/admin/control/$view/view/$type"
    }

}
