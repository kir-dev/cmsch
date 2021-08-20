package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dto.virtual.DebtsByGroup
import hu.bme.sch.g7.dto.virtual.FileVirtualEntity
import hu.bme.sch.g7.dto.virtual.FilesByView
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.service.ClockService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Path
import javax.servlet.http.HttpServletRequest
import kotlin.io.path.fileSize
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence
import kotlin.streams.toList

const val CONTROL_MODE_FILE = "file"

@Controller
@RequestMapping("/admin/control/files")
class FilesByViewController(
        @Value("\${g7web.external:/etc/g7web/external/}") private val root: String,
        private val clock: ClockService
) {

    private val view = "files"
    private val titleSingular = "Fájl"
    private val titlePlural = "Fájlok"
    private val description = "Fájlok kategóriánként csoportosítva"

    private val overviewDescriptor = OverviewBuilder(FilesByView::class)
    private val submittedDescriptor = OverviewBuilder(FileVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<FilesByView> {
        return sequenceOf("profiles", "news", "events", "products", "groups", "achievement")
                .filter { Files.exists(Path.of(root, it)) }
                .map { FilesByView(it, Files.walk(Path.of(root, it)).count() - 1, "~${Path.of(root, it).fileSize() / 1024} KB") }
                .toList()

    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: String, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
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
        model.addAttribute("user", request.getUser())
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
        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", "$type/$id")
        model.addAttribute("user", request.getUser())
        model.addAttribute("item", id)

        return "delete"
    }

    @PostMapping("/delete/{type}/{id}")
    fun delete(@PathVariable type: String, @PathVariable id: String, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        Files.deleteIfExists(Path.of(root, type, id))

        return "redirect:/admin/control/$view/view/$type"
    }

}