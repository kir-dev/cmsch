package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.urlEncode
import jakarta.annotation.PostConstruct
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.math.absoluteValue

@Controller
@RequestMapping("/admin/control/upload-file")
class FileUploadController(
    private val adminMenuService: AdminMenuService,
    private val applicationComponent: ApplicationComponent,
    private val auditLog: AuditLogService,
    private val storageService: StorageService
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
        model.addAttribute("baseUrl", applicationComponent.adminSiteUrl)
        model.addAttribute("permission", permissionControl.permissionString)

        return "uploadFile"
    }

    @PostMapping("")
    fun uploadImage(
        model: Model,
        auth: Authentication,
        @RequestParam names: List<String>,
        @RequestParam keepNames: Boolean?,
        @RequestBody files: List<MultipartFile>
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, "file-upload", "POST /upload-file", permissionControl.permissionString)
            return "admin403"
        }

        if (names.size != files.size) {
            throw IllegalArgumentException("The length of names and files does not match")
        }
        val newNames = mutableListOf<String>()
        for ((file, name) in files.zip(names)) {
            val newName = renameFile(keepNames, file.originalFilename ?: "", name)
            storageService.saveNamedObject("public", newName, file).ifPresent { newNames.add(it) }
        }
        val links = newNames.joinToString(",") { it.urlEncode() }
        return "redirect:/admin/control/upload-file?uploaded=${links}"
    }

    private fun renameFile(keepNames: Boolean?, originalFilename: String, name: String): String {
        val newName = if (keepNames == true && name.isNotBlank())
            name
        else
            name.replace(" ", "_")
                .replace(Regex("[^A-Za-z0-9_]+"), "")
                .uppercase() + "_${Random().nextLong().absoluteValue.toString(36).uppercase()}"

        val extension = originalFilename.substringAfterLast(".")
        return "${newName.urlEncode()}.$extension"
    }

}
