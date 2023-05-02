package hu.bme.sch.cmsch.controller.admin

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.INPUT_TYPE_FILE
import hu.bme.sch.cmsch.admin.INTERPRETER_INHERIT
import hu.bme.sch.cmsch.admin.INTERPRETER_SEARCH
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.IdentifiableEntity
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import hu.bme.sch.cmsch.util.uploadFile
import org.slf4j.LoggerFactory
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

data class ControlAction(
    val name: String,
    val endpoint: String,
    val icon: String,

    @get:JsonIgnore
    val permission: PermissionValidator,

    val order: Int,
    val newPage: Boolean = false,
    var usageString: String = "",
    var basic: Boolean = false
)

data class ButtonAction(
    val name: String,
    val target: String,

    @get:JsonIgnore
    val permission: PermissionValidator,
    val order: Int,
    val icon: String,
    val primary: Boolean = false,
    val newPage: Boolean = false
)

const val INVALID_ID_ERROR = "Object with this id was not found in the database"

open class OneDeepEntityPage<T : IdentifiableEntity>(
    internal val view: String,
    classType: KClass<T>,
    private val supplier: Supplier<T>,
    internal val titleSingular: String,
    internal val titlePlural: String,
    internal val description: String,

    internal val dataSource: EntityPageDataSource<T, Int>,
    internal val importService: ImportService,
    internal val adminMenuService: AdminMenuService,
    internal val component: ComponentBase,
    internal val auditLog: AuditLogService,
    internal val objectMapper: ObjectMapper,
    internal val entitySourceMapping: Map<String, (T?) -> List<String>> =
        mapOf(Nothing::class.simpleName!! to { listOf() }),

    internal val showPermission: PermissionValidator,
    internal val createPermission: PermissionValidator,
    internal val editPermission: PermissionValidator,
    internal val deletePermission: PermissionValidator,

    internal val showEnabled: Boolean = true,
    internal val createEnabled: Boolean = false,
    internal val editEnabled: Boolean = false,
    internal val deleteEnabled: Boolean = false,
    internal val importEnabled: Boolean = true,
    internal val exportEnabled: Boolean = true,

    private val adminMenuCategory: String? = null,
    private val adminMenuIcon: String = "check_box_outline_blank",
    private val adminMenuPriority: Int = 1,

    internal val controlActions: MutableList<ControlAction> = mutableListOf(),
    internal val buttonActions: MutableList<ButtonAction> = mutableListOf()
) {

    private val log = LoggerFactory.getLogger(javaClass)
    internal val descriptor = OverviewBuilder(classType)

    @PostConstruct
    fun init() {
        val category = adminMenuCategory ?: component.javaClass.simpleName
        adminMenuService.registerEntry(category, AdminMenuEntry(
            titlePlural,
            adminMenuIcon,
            "/admin/control/${view}",
            adminMenuPriority,
            showPermission
        ))

        if (createEnabled) {
            buttonActions.add(ButtonAction(
                "Új $titleSingular",
                "create",
                createPermission,
                100,
                "add_box",
                true
            ))
            if (importEnabled || exportEnabled) {
                buttonActions.add(ButtonAction(
                    "Import / Export",
                    "resource",
                    createPermission,
                    200,
                    "upload_file",
                    false
                ))
            }
        }

        if (editEnabled) {
            controlActions.add(ControlAction(
                "Szerkesztés",
                "edit/{id}",
                "edit",
                editPermission,
                100,
                usageString = "Bejegyzés szerkesztése",
                basic = true
            ))
        } else if (showEnabled) {
            controlActions.add(ControlAction(
                "Megtekintés",
                "show/{id}",
                "visibility",
                showPermission,
                100,
                usageString = "Bejegyzés megnyitása",
                basic = true
            ))
        }

        if (deleteEnabled) {
            controlActions.add(ControlAction(
                "Törlés",
                "delete/{id}",
                "delete",
                deletePermission,
                200,
                usageString = "Bejegyzés törlése",
                basic = true
            ))
            buttonActions.add(ButtonAction(
                "Összes törlése",
                "purge",
                deletePermission,
                300,
                "delete_forever",
                false
            ))
        }

        controlActions.sortBy { it.order }
        buttonActions.sortBy { it.order }
    }

    @GetMapping("")
    open fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", descriptor.getColumnsAsJson())
        model.addAttribute("tableData", descriptor.getTableDataAsJson(filterOverview(user, fetchOverview(user))))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", descriptor.toJson(
            controlActions.filter { it.permission.validate(user) },
            objectMapper))
        model.addAttribute("allControlActions", controlActions)
        model.addAttribute("buttonActions", buttonActions.filter { it.permission.validate(user) })

        attachPermissionInfo(model)

        return "overview4"
    }

    internal fun attachPermissionInfo(model: Model) {
        if (showEnabled && showPermission != ImplicitPermissions.PERMISSION_NOBODY)
            model.addAttribute("permissionShow", showPermission.permissionString)

        if (editEnabled && editPermission != ImplicitPermissions.PERMISSION_NOBODY)
            model.addAttribute("permissionEdit", editPermission.permissionString)

        if (createEnabled && createPermission != ImplicitPermissions.PERMISSION_NOBODY)
            model.addAttribute("permissionCreate", createPermission.permissionString)

        if (deleteEnabled && deletePermission != ImplicitPermissions.PERMISSION_NOBODY)
            model.addAttribute("permissionDelete", deletePermission.permissionString)

        model.addAttribute("importEnabled", importEnabled)
        model.addAttribute("exportEnabled", exportEnabled)
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/edit/$id", showPermission.permissionString)
            return "admin403"
        }

        if (!editEnabled)
            return "redirect:/admin/control/$view/"

        val entity = dataSource.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            val actualEntity = onPreEdit(entity.orElseThrow())
            model.addAttribute("data", actualEntity)
            if (!editPermissionCheck(user, actualEntity)) {
                model.addAttribute("user", user)
                auditLog.admin403(user, component.component, "GET /${view}/edit/$id",
                    "editPermissionCheck() validation")
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
        model.addAttribute("readOnly", false)

        onDetailsView(user, model)
        return "details"
    }

    @GetMapping("/show/{id}")
    fun show(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/show/$id", showPermission.permissionString)
            return "admin403"
        }

        val entity = dataSource.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        }
        model.addAttribute("data", entity.orElseThrow())

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("readOnly", true)

        onDetailsView(user, model)
        return "details"
    }

    @GetMapping("/create")
    fun create(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (createPermission.validate(user).not()) {
            model.addAttribute("permission", createPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/create", createPermission.permissionString)
            return "admin403"
        }

        if (createEnabled.not())
            return "redirect:/admin/control/$view/"

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", false)
        model.addAttribute("view", view)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("data", null)
        model.addAttribute("user", user)
        model.addAttribute("readOnly", false)

        onDetailsView(user, model)
        return "details"
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (deletePermission.validate(user).not()) {
            model.addAttribute("permission", deletePermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/delete/$id",
                deletePermission.permissionString)
            return "admin403"
        }

        if (!deleteEnabled)
            return "redirect:/admin/control/$view/"

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", user)

        val entity = dataSource.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            val actualEntity = entity.orElseThrow()
            model.addAttribute("item", actualEntity.toString())
            if (!editPermissionCheck(user, actualEntity)) {
                model.addAttribute("user", user)
                auditLog.admin403(user, component.component, "GET /${view}/delete/$id",
                    "editPermissionCheck() validation")
                return "admin403"
            }
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (deletePermission.validate(user).not()) {
            model.addAttribute("permission", deletePermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /${view}/delete/$id", deletePermission.permissionString)
            return "admin403"
        }

        val entity = dataSource.findById(id).orElseThrow()
        if (!editPermissionCheck(user, entity)) {
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /${view}/delete/$id", "editPermissionCheck() validation")
            return "admin403"
        }

        if (!deleteEnabled)
            return "redirect:/admin/control/$view/"

        auditLog.delete(user, component.component, "delete ${entity.id} $entity")
        dataSource.delete(entity)
        onEntityDeleted(entity)
        return "redirect:/admin/control/$view/"
    }

    @GetMapping("/purge")
    fun purge(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (!deleteEnabled || deletePermission.validate(user).not()) {
            model.addAttribute("permission", deletePermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/purge", deletePermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        return "purge"
    }

    @PostMapping("/purge")
    fun purgeConfirmed(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (!deleteEnabled || deletePermission.validate(user).not()) {
            log.info("User '{}'#{} wanted to purge view '{}'", user.userName, user.id, view)
            throw IllegalStateException("Insufficient permissions")
        }

        log.info("Purging view '{}' by user '{}'#{}", view, user.userName, user.id)
        val before = dataSource.count()
        try {
            purgeAllEntities(user)
        } catch (e : Exception) {
            auditLog.delete(user, component.component, "purge all in $view failed: ${e.message}")
            log.error("Purging failed on view '{}'", view, e)
        }
        val after = dataSource.count()
        model.addAttribute("purgedCount", before - after)
        log.info("Purged {} on view '{}'", before - after, view)
        auditLog.delete(user, component.component, "purge all in $view (affected: ${before - after})")

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        adminMenuService.addPartsForMenu(user, model)
        return "purge"
    }

    @PostMapping("/create")
    fun create(@ModelAttribute(binding = false) dto: T,
               @RequestParam(required = false) file0: MultipartFile?,
               @RequestParam(required = false) file1: MultipartFile?,
               model: Model,
               auth: Authentication
    ): String {
        val user = auth.getUser()
        if (createPermission.validate(user).not()) {
            model.addAttribute("permission", createPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /${view}/create", createPermission.permissionString)
            return "admin403"
        }

        if (!createEnabled)
            return "redirect:/admin/control/$view/"

        val entity = supplier.get()
        val newValues = StringBuilder("entity new value: ")
        updateEntity(descriptor, user, entity, dto, newValues, file0, file1)
        entity.id = 0
        if (onEntityPreSave(entity, auth)) {
            auditLog.create(user, component.component, newValues.toString())
            dataSource.save(entity)
        }
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
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /${view}/edit/${id}", editPermission.permissionString)
            return "admin403"
        }

        val entity = dataSource.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }
        val actualEntity = entity.orElseThrow()
        if (!editPermissionCheck(user, actualEntity)) {
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /${view}/edit/${id}", "editPermissionCheck() validation")
            return "admin403"
        }

        if (!editEnabled)
            return "redirect:/admin/control/$view/"

        val newValues = StringBuilder("entity new value: ")
        updateEntity(descriptor, user, actualEntity, dto, newValues, file0, file1)
        actualEntity.id = id
        if (onEntityPreSave(actualEntity, auth)) {
            auditLog.edit(user, component.component, newValues.toString())
            dataSource.save(actualEntity)
        }
        onEntityChanged(actualEntity)
        return "redirect:/admin/control/$view"
    }

    internal fun updateEntity(
        descriptor: OverviewBuilder<T>, user: CmschUser, entity: T, dto: T,
        newValues: StringBuilder, file0: MultipartFile?, file1: MultipartFile?
    ) {
        descriptor.getInputs().forEach {
            if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore && it.second.minimumRole.value <= user.role.value) {
                when {
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type == INPUT_TYPE_FILE -> {
                        when (it.second.fileId) {
                            "0" -> {
                                file0?.uploadFile(view)?.let { file ->
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "$view/$file")
                                    newValues.append(it.first.name).append("=name@").append(view)
                                        .append("/").append(file).append(", ")
                                }
                            }
                            "1" -> {
                                file1?.uploadFile(view)?.let { file ->
                                    newValues.append(it.first.name).append("=name@").append(view)
                                        .append("/").append(file).append(", ")
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "$view/$file")
                                }
                            }
                            else -> {
                                log.error("Invalid file field name: file${it.second.fileId}")
                            }
                        }
                    }
                    (it.second.interpreter == INTERPRETER_INHERIT || it.second.interpreter == INTERPRETER_SEARCH) && it.second.type != INPUT_TYPE_FILE -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto))
                        newValues.append(it.first.name).append("=").append(it.first.getter.call(dto)?.toString()
                            ?.replace("\r", "")?.replace("\n", "") ?: "<null>").append(", ")
                    }
                    it.second.interpreter == "path" -> {
                        val value = it.first.getter.call(dto)
                            ?.toString()
                            ?.lowercase()
                            ?.replace(" ", "-")
                            ?.replace(Regex("[^a-z0-9-.]"), "") ?: "<null>"
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, value)
                        newValues.append(it.first.name).append("=").append(value).append(", ")
                    }
                }
            }
        }
    }

    @GetMapping("/resource")
    fun resource(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (createPermission.validate(user).not() || showPermission.validate(user).not()) {
            model.addAttribute("permission", createPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}/resource", createPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("view", view)
        model.addAttribute("user", user)
        model.addAttribute("importEnabled", importEnabled && createEnabled && createPermission.validate(user))
        model.addAttribute("exportEnabled", exportEnabled && showPermission.validate(user))
        return "resource"
    }

    @ResponseBody
    @GetMapping("/export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun export(auth: Authentication, response: HttpServletResponse): ByteArray {
        val user = auth.getUserFromDatabase()
        if (!exportEnabled || !showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-export.csv\"")
        return descriptor.exportToCsv(filterOverview(user, fetchOverview(user)).toList()).toByteArray()
    }

    @PostMapping("/import/csv")
    fun import(file: MultipartFile?, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (!importEnabled || !createEnabled || !createPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val out = ByteArrayOutputStream()
        file?.inputStream?.transferTo(out)
        val rawEntities = out.toString().split("\n").stream()
            .map { entity -> entity.split(";").map { it.trim() } }
            .skip(1)
            .toList()
        log.info("Importing {} bytes ({} lines) into {}", out.size(), rawEntities.size, view)
        val before = dataSource.count()
        importService.importEntities(dataSource, rawEntities, supplier, descriptor.getImportModifiers())
        val after = dataSource.count()
        model.addAttribute("importedCount", after - before)
        auditLog.create(user, component.component, "imported $view (new entities: ${after - before})")

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

    open fun onPreEdit(actualEntity: T): T {
        // Overridden when notification is required
        return actualEntity
    }

    open fun filterOverview(user: CmschUser, rows: Iterable<T>): Iterable<T> {
        return rows
    }

    open fun editPermissionCheck(user: CmschUser, entity: T): Boolean {
        return true
    }

    open fun purgeAllEntities(user: CmschUser) {
        dataSource.deleteAll()
    }

    open fun fetchOverview(user: CmschUser): Iterable<T> {
        return dataSource.findAll()
    }

}