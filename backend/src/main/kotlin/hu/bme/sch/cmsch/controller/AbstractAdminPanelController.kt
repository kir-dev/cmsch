package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.admin.INPUT_TYPE_FILE
import hu.bme.sch.cmsch.admin.INTERPRETER_INHERIT
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_IMPORT_EXPORT
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_PURGE
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.uploadFile
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.function.Supplier
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

const val INVALID_ID_ERROR = "INVALID_ID"
const val CONTROL_MODE_EDIT_DELETE = "edit,delete"
const val CONTROL_MODE_DELETE = "delete"
const val CONTROL_MODE_EDIT = "edit"
const val CONTROL_MODE_VIEW = "view"
const val CONTROL_MODE_VIEW_EXPORT2 = "view,export2"
const val CONTROL_MODE_LOCATION = "location"

open class AbstractAdminPanelController<T : ManagedEntity>(
        private val repo: CrudRepository<T, Int>?,
        private val view: String,
        private val titleSingular: String,
        private val titlePlural: String,
        private val description: String,
        classType: KClass<T>,
        private val supplier: Supplier<T>,
        private val importService: ImportService,
        private val adminMenuService: AdminMenuService,
        private val component: ComponentBase,
        private val entitySourceMapping: Map<String, (T?) -> List<String>> =
                mapOf(Nothing::class.simpleName!! to { listOf() }),
        private val controlMode: String = CONTROL_MODE_EDIT_DELETE,
        private val permissionControl: PermissionValidator,
        private val importable: Boolean = false,
        private val adminMenuIcon: String = "check_box_outline_blank",
        private val adminMenuPriority: Int = 1,
        private val virtualEntity: Boolean = false,
        private val allowedToPurge: Boolean = importable,
        private val savable: Boolean = false,
        private val purgeRepo: CrudRepository<*, Int>? = repo,
) : AbstractPurgeAdminPageController<T>(repo, adminMenuService, titlePlural, view, allowedToPurge) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val descriptor = OverviewBuilder(classType)

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
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", filterOverview(user, fetchOverview()))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", controlMode)
        model.addAttribute("importable", importable && PERMISSION_IMPORT_EXPORT.validate(user))
        model.addAttribute("allowedToPurge", allowedToPurge && PERMISSION_PURGE.validate(user))
        model.addAttribute("savable", savable)

        return "overview"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        val entity = repo?.findById(id) ?: findByIdFallback(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            val actualEntity = entity.orElseThrow()
            model.addAttribute("data", actualEntity)
            if (!editPermissionCheck(user, actualEntity)) {
                model.addAttribute("user", user)
                return "admin403"
            }
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("controlMode", controlMode)

        onDetailsView(user, model)
        return "details"
    }

    @GetMapping("/create")
    fun create(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", false)
        model.addAttribute("view", view)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("data", null)
        model.addAttribute("user", user)
        model.addAttribute("controlMode", controlMode)

        onDetailsView(user, model)
        return "details"
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", user)

        val entity = repo?.findById(id) ?: findByIdFallback(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            val actualEntity = entity.orElseThrow()
            model.addAttribute("item", actualEntity.toString())
            if (!editPermissionCheck(user, actualEntity)) {
                model.addAttribute("user", user)
                return "admin403"
            }
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = repo?.findById(id)?.orElseThrow() ?: findByIdFallback(id).orElseThrow()
        if (!editPermissionCheck(user, entity)) {
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        repo?.delete(entity)
        onEntityDeleted(entity)
        return "redirect:/admin/control/$view/"
    }

    @PostMapping("/create")
    fun create(@ModelAttribute(binding = false) dto: T,
               @RequestParam(required = false) file0: MultipartFile?,
               @RequestParam(required = false) file1: MultipartFile?,
               model: Model,
               auth: Authentication
    ): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        val entity = supplier.get()
        updateEntity(descriptor, user, entity, dto, file0, file1)
        entity.id = 0
        if (onEntityPreSave(entity, auth))
            repo?.save(entity)
        onEntityChanged(entity)
        return "redirect:/admin/control/$view/"
    }

    @PostMapping("/edit/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: T,
             @RequestParam(required = false) file0: MultipartFile?,
             @RequestParam(required = false) file1: MultipartFile?,
             model: Model,
             auth: Authentication
    ): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = repo?.findById(id) ?: findByIdFallback(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }
        val actualEntity = entity.orElseThrow()
        if (!editPermissionCheck(user, actualEntity)) {
            model.addAttribute("user", user)
            return "admin403"
        }

        if (virtualEntity)
            return "redirect:/admin/control/$view/"

        updateEntity(descriptor, user, actualEntity, dto, file0, file1)
        actualEntity.id = id
        if (onEntityPreSave(actualEntity, auth))
            repo?.save(actualEntity)
        onEntityChanged(actualEntity)
        return "redirect:/admin/control/$view"
    }

    private fun updateEntity(descriptor: OverviewBuilder, user: CmschUser, entity: T, dto: T, file0: MultipartFile?, file1: MultipartFile?) {
        descriptor.getInputs().forEach {
            if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore && it.second.minimumRole.value <= user.role.value) {
                when {
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type == INPUT_TYPE_FILE -> {
                        when (it.second.fileId) {
                            "0" -> {
                                file0?.uploadFile(view)?.let { file ->
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "$view/$file")
                                }
                            }
                            "1" -> {
                                file1?.uploadFile(view)?.let { file ->
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "$view/$file")
                                }
                            }
                            else -> {
                                log.error("Invalid file field name: file${it.second.fileId}")
                            }
                        }
                    }
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type != INPUT_TYPE_FILE -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto))
                    }
                    it.second.interpreter == "path" -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto)
                                .toString()
                                .lowercase()
                                .replace(" ", "-")
                                .replace(Regex("[^a-z0-9-.]"), ""))
                    }

                }
            }
        }
    }

    @GetMapping("/resource")
    fun resource(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (!importable || PERMISSION_IMPORT_EXPORT.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_IMPORT_EXPORT.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        return "resource"
    }

    @ResponseBody
    @GetMapping("/export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun export(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (!importable || PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-export.csv\"")
        return descriptor.exportToCsv(fetchOverview().toList()).toByteArray()
    }

    @ResponseBody
    @GetMapping("/save/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun saveAsCsv(auth: Authentication, response: HttpServletResponse): ByteArray {
        if (!savable || PERMISSION_IMPORT_EXPORT.validate(auth.getUser()).not()) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-export.csv\"")
        return descriptor.exportToCsv(fetchOverview().toList()).toByteArray()
    }

    @PostMapping("/import/csv")
    fun import(file: MultipartFile?, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (!importable || PERMISSION_IMPORT_EXPORT.validate(user).not()) {
            throw IllegalStateException("Insufficient permissions")
        }

        if (repo != null) {
            val out = ByteArrayOutputStream()
            file?.inputStream?.transferTo(out)
            val rawEntities = out.toString().split("\n").stream()
                .map { entity -> entity.split(";").map { it.trim() } }
                .skip(1)
                .toList()
            log.info("Importing {} bytes ({} lines) into {}", out.size(), rawEntities.size, view)
            val before = repo.count()
            importService.importEntities(repo, rawEntities, supplier, descriptor.getImportModifiers())
            val after = repo.count()
            model.addAttribute("importedCount", after - before)
        } else {
            log.info("Importing is disabled for virtual entities (on view: {})", view)
            model.addAttribute("importedCount", 0)
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        adminMenuService.addPartsForMenu(user, model)
        return "resource"
    }

    open fun onEntityChanged(entity: T) {
        // Overridden when notification is required
    }

    open fun onEntityDeleted(entity: T) {
        // Overridden when notification is required
    }

    open fun onEntityPreSave(entity: T, auth: Authentication): Boolean {
        // Overridden when notification is required
        return true
    }

    open fun onDetailsView(entity: CmschUser, model: Model) {
        // Overridden when notification is required
    }

    open fun filterOverview(user: CmschUser, rows: Iterable<T>): Iterable<T> {
        return rows
    }

    open fun editPermissionCheck(user: CmschUser, entity: T): Boolean {
        return true
    }

    open fun findByIdFallback(id: Int): Optional<T> {
        return Optional.empty()
    }

    open fun fetchOverview(): Iterable<T> {
        return repo?.findAll() ?: mutableListOf()
    }

}
