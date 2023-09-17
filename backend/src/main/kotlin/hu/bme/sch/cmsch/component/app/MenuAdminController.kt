package hu.bme.sch.cmsch.component.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.model.IdentifiableEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.net.URLEncoder

@Controller
@RequestMapping("/admin/control/menu")
@ConditionalOnBean(ApplicationComponent::class)
class MenuAdminController(
    private val adminMenuService: AdminMenuService,
    private val menuService: MenuService,
    private val objectMapper: ObjectMapper,
    private val auditLogService: AuditLogService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val view = "menu"
    private val titleSingular = "Menü beállítások"
    private val titlePlural = "Menü beállítások"
    private val description = "Egyes roleokkal hogy nézzen ki a menü"
    private val showPermission = ControlPermissions.PERMISSION_CONTROL_APP
    private val editPermission = ControlPermissions.PERMISSION_CONTROL_APP

    private val overviewDescriptor = OverviewBuilder(MenuSetupByRoleVirtualEntity::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()
    private val buttonActions: MutableList<ButtonAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.CONTENT_CATEGORY, AdminMenuEntry(
                titlePlural,
                "menu_open",
                "/admin/control/${view}",
                3,
                showPermission
            )
        )
        controlActions.add(
            ControlAction(
                "Megnyitás",
                "edit/{id}",
                "edit",
                editPermission,
                100,
                usageString = "Menü testreszabása",
                basic = true
            )
        )
        buttonActions.add(
            ButtonAction(
                "Export",
                "export-csv",
                showPermission,
                100,
                "download",
                primary = false,
                newPage = true
            )
        )
        buttonActions.add(
            ButtonAction(
                "Import",
                "import-csv",
                editPermission,
                200,
                "upload",
                primary = false
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
            auditLogService.admin403(user, "menu", "GET /menu", showPermission.permissionString)
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
        model.addAttribute("buttonActions", buttonActions)

        return "overview4"
    }

    private fun fetchOverview(): List<MenuSetupByRoleVirtualEntity> {
        return RoleType.values()
            .filter { it.value < 10000 }
            .map { MenuSetupByRoleVirtualEntity(it.ordinal, it.name, it.description) }
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, "menu", "GET /$view/edit/$id", showPermission.permissionString)
            return "admin403"
        }
        val role = RoleType.values()[id]
        model.addAttribute("id", id)
        model.addAttribute("role", role)
        model.addAttribute("rows", menuService.getMenusForRole(role).sortedBy { it.order })

        return "menuSettings"
    }

    @PostMapping("/edit/{id}")
    fun editFormTarget(@PathVariable id: Int, auth: Authentication, model: Model, @RequestParam allRequestParams: Map<String, String>): String {
        val user = auth.getUser()
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, "menu", "POST /$view/edit/$id", editPermission.permissionString)
            return "admin403"
        }

        val menus = mutableListOf<MenuSettingItem>()
        var i = 0
        while (allRequestParams["id_$i"] != null && allRequestParams["order_$i"] != null) {
            menus.add(MenuSettingItem(id = allRequestParams["id_$i"]!!,
                name ="", url = "",
                order = allRequestParams["order_$i"]?.toIntOrNull() ?: 0,
                visible = allRequestParams["visible_$i"] != null && allRequestParams["visible_$i"] != "off",
                subMenu = allRequestParams["submenu_$i"] != null && allRequestParams["submenu_$i"] != "off"
            ))
            i++
        }
        auditLogService.edit(user, "menu", menus.toString())
        menuService.persistSettings(menus, RoleType.values()[id])
        return "redirect:/admin/control/menu/edit/$id"
    }

    @ResponseBody
    @GetMapping("/export-csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun exportCsv(auth: Authentication, response: HttpServletResponse): ByteArray {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val csvMapper = CsvMapper()
        val csvSchema = csvMapper.schemaFor(MenuService.MenuImportEntry::class.java)
            .withHeader()
            .withColumnSeparator(',')
        val outputStream = ByteArrayOutputStream()

        csvMapper.writer(csvSchema).writeValue(outputStream, menuService.exportMenu())

        response.setHeader("Content-Disposition", "attachment; filename=\"$view.csv\"")
        return outputStream.toByteArray()
    }

    @GetMapping("/import-csv")
    fun importCsv(
        model: Model,
        auth: Authentication,
        @RequestParam(defaultValue = "") imported: String,
        @RequestParam(defaultValue = "") notAffected: String,
        @RequestParam(defaultValue = "") error: String,
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, "menu", "GET /$view/import-csv", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("view", view)
        model.addAttribute("importedCount", imported.toIntOrNull())
        model.addAttribute("notAffectedCount", notAffected.toIntOrNull())
        model.addAttribute("error", error.ifBlank { null })
        return "importMenu"
    }

    @PostMapping("/import-csv")
    fun importCsv(
        auth: Authentication,
        @RequestParam params: MultiValueMap<String, String>,
        @RequestPart("file") file: MultipartFile
    ): String {
        val user = auth.getUser()
        if (!editPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val roles = RoleType.values().filter { params.containsKey(it.name) }
        val menuEntries = mutableListOf<MenuService.MenuImportEntry>()

        try {
            val csvMapper = CsvMapper()
            val csvSchema = CsvSchema.emptySchema()
                .withHeader()
                .withColumnSeparator(',')
            val reader = csvMapper.readerFor(MenuService.MenuImportEntry::class.java).with(csvSchema)

            val iterator = reader.readValues<MenuService.MenuImportEntry>(file.inputStream)
            while (iterator.hasNext()) {
                val menuEntry = iterator.next()
                menuEntries.add(menuEntry)
            }

            val (imported, notAffected) = menuService.importMenu(menuEntries, roles)
            val action = "Imported $imported menus ($notAffected not affected) " +
                    "for roles: ${roles.joinToString(", ")}"
            auditLogService.edit(user, view, action)
            log.info("{}: {}", user.userName, action)
            return "redirect:/admin/control/${view}/import-csv?imported=$imported&notAffected=$notAffected"

        } catch (e: Exception) {
            auditLogService.error(view, "Failed to import menus: ${e.message}")
            log.error("{}: {}", user.userName, e.message, e)
            return "redirect:/admin/control/${view}/import-csv?error=${URLEncoder.encode(e.message, "UTF-8")}"
        }
    }
}

class MenuSetupByRoleVirtualEntity(
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @property:GenerateOverview(columnName = "Role", order = 1)
    var role: String = "",

    @property:GenerateOverview(columnName = "Leírás", order = 2)
    var description: String = "",
) : IdentifiableEntity
