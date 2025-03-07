package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.virtual.FileVirtualEntity
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.HandlerMapping

// todo XDDDDDDDDD

@Controller
@RequestMapping("/admin/control/files")
class FilesByViewController(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val adminMenuService: AdminMenuService,
    private val auditLog: AuditLogService,
    private val storageService: StorageService
) {


    private val view = "files"
    private val titleSingular = "Fájl"
    private val titlePlural = "Fájlok"
    private val description = "Fájlok kategóriánként csoportosítva"
    private val showPermission = ControlPermissions.PERMISSION_SHOW_FILES
    private val deletePermission = ControlPermissions.PERMISSION_DELETE_FILES

    private val submittedDescriptor = OverviewBuilder(FileVirtualEntity::class)

    private val controlActions: MutableList<ControlAction> = mutableListOf()

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.CONTENT_CATEGORY, AdminMenuEntry(
                titlePlural,
                "folder",
                "/admin/control/$view",
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

        model.addAttribute("columnData", submittedDescriptor.getColumns())
        model.addAttribute("tableData", submittedDescriptor.getTableData(listFilesInView()))

        model.addAttribute("user", user)
        val controlActionForCategory = listOf(
            ControlAction(
                "Megnyitás",
                "cdn/{id}",
                "visibility",
                showPermission,
                100,
                true,
                usageString = "File megnyitása",
                basic = true
            ),
            ControlAction(
                "Törlés",
                "delete/{id}",
                "delete",
                deletePermission,
                200,
                false,
                usageString = "File törlése",
                basic = true
            )
        )
        model.addAttribute("controlActions", controlActionForCategory.filter { it.permission.validate(user) })
        model.addAttribute("allControlActions", controlActionForCategory)
        model.addAttribute("buttonActions", listOf<ButtonAction>())

        return "overview4"
    }

    private fun listFilesInView(): List<FileVirtualEntity> {
        return storageService.listObjects().map { FileVirtualEntity(it.first, "${it.second / 1024} KB") }
    }

    @GetMapping("/cdn/**")
    fun redirectCdn(request: HttpServletRequest): String {
        val requestPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val objectPath = requestPath.split("/admin/control/files/cdn/")[1]
        return storageService.getObjectUrl(objectPath).map { "redirect:$it" }.orElse("error-404")
    }

    @GetMapping("/delete/**")
    fun deleteConfirm(model: Model, auth: Authentication, request: HttpServletRequest): String {
        val requestPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val objectPath = requestPath.split("/admin/control/files/delete/")[1]
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", objectPath)
        model.addAttribute("user", user)
        model.addAttribute("item", objectPath)

        return "delete"
    }

    @PostMapping("/delete/**")
    fun delete(request: HttpServletRequest, model: Model, auth: Authentication): String {
        val requestPath = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val objectPath = requestPath.split("/admin/control/files/delete/")[1]
        val user = auth.getUser()
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        auditLog.delete(user, "files", objectPath)
        storageService.deleteObject(objectPath)

        return "redirect:/admin/control/$view"
    }

}
