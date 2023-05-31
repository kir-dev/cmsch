package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.uploadFile
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import jakarta.annotation.PostConstruct
import kotlin.math.absoluteValue

@Controller
@RequestMapping("/admin/control/upload-file")
class FileUploadController(
    private val adminMenuService: AdminMenuService,
    private val applicationComponent: ApplicationComponent,
    private val auditLog: AuditLogService
) {

    private val permissionControl = ControlPermissions.PERMISSION_UPLOAD_FILES

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.CONTENT_CATEGORY, AdminMenuEntry(
                "Fájlfeltöltés",
                "cloud_upload",
                "/admin/control/upload-file",
                2,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication, @RequestParam(defaultValue = "no") uploaded: String): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, "file-upload", "GET /upload-file", permissionControl.permissionString)
            return "admin403"
        }

        model.addAttribute("user", user)
        model.addAttribute("uploaded", uploaded)
        model.addAttribute("baseUrl", applicationComponent.adminSiteUrl.getValue())
        model.addAttribute("permission", permissionControl.permissionString)

        return "uploadFile"
    }

    @PostMapping("")
    fun uploadImage(model: Model, auth: Authentication, @RequestParam name: String, @RequestBody file: MultipartFile?): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, "file-upload", "POST /upload-file", permissionControl.permissionString)
            return "admin403"
        }

        val originalFilename = file?.originalFilename ?: ""
        val newName = name.replace(" ", "_").replace(Regex("[^A-Za-z0-9_]+"), "").uppercase() +
                "_${Random().nextLong().absoluteValue.toString(36).uppercase()}" +
                originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0)
        file?.uploadFile("public", newName)

        return "redirect:/admin/control/upload-file?uploaded=${newName}"
    }

}
