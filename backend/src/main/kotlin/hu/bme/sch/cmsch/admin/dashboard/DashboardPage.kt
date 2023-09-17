package hu.bme.sch.cmsch.admin.dashboard

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.ByteArrayOutputStream

abstract class DashboardPage(
    internal var view: String,
    internal var title: String,
    internal var description: String,
    internal var wide: Boolean,

    private var adminMenuService: AdminMenuService,
    internal var component: ComponentBase,
    internal var auditLog: AuditLogService,

    internal var showPermission: PermissionValidator,

    private var adminMenuCategory: String? = null,
    private var adminMenuIcon: String = "check_box_outline_blank",
    private var adminMenuPriority: Int = 1,
    private var ignoreFromMenu: Boolean = false
) {

    abstract fun getComponents(user: CmschUser): List<DashboardComponent>

    @PostConstruct
    fun init() {
        val category = adminMenuCategory ?: component.javaClass.simpleName
        if (!ignoreFromMenu) {
            adminMenuService.registerEntry(
                category, AdminMenuEntry(
                    title,
                    adminMenuIcon,
                    "/admin/control/${view}",
                    adminMenuPriority,
                    showPermission
                )
            )
        }
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication, @RequestParam(defaultValue = "-1") card: Int, @RequestParam(defaultValue = "") message: String): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", getComponents(user))
        model.addAttribute("user", user)
        model.addAttribute("card", card)
        model.addAttribute("message", message)

        return "dashboard"
    }

    @ResponseBody
    @GetMapping("/export/{id}", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun export(auth: Authentication, response: HttpServletResponse, @PathVariable id: Int): ByteArray {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        val outputStream = ByteArrayOutputStream()
        val components = getComponents(user)
        val exportable = components.firstOrNull() { it.id == id }
        if (exportable == null || exportable !is DashboardTableCard || !exportable.exportable)
            return outputStream.toByteArray()

        val csvMapper = CsvMapper()
        val csvSchemaBuilder = CsvSchema.builder()
            .setUseHeader(true)
            .setReorderColumns(true)

        exportable.header.forEach {
            csvSchemaBuilder.addColumn(it)
        }

        val csvSchema = csvSchemaBuilder.build()
            .withQuoteChar('"')
            .withEscapeChar('\\')
            .withColumnSeparator(',')

        csvMapper.writerFor(List::class.java)
            .with(csvSchema)
            .writeValue(outputStream, exportable.content)

        response.setHeader("Content-Disposition", "attachment; filename=\"${exportable.fileName()}.csv\"")
        return outputStream.toByteArray()
    }

}