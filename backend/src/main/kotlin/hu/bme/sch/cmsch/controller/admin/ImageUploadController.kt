package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_SHOW_DELETE_FILES
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.uploadFile
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.annotation.PostConstruct
import kotlin.math.absoluteValue

@Controller
@RequestMapping("/admin/control/upload-file")
class ImageUploadController(
    private val startupPropertyConfig: StartupPropertyConfig,
    private val adminMenuService: AdminMenuService
) {

    private val permissionControl = PERMISSION_SHOW_DELETE_FILES

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent::class.simpleName!!, AdminMenuEntry(
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
            return "admin403"
        }

        model.addAttribute("user", user)
        model.addAttribute("uploaded", uploaded == "ok")

        return "uploadFile"
    }

    @PostMapping("")
    fun uploadImage(model: Model, auth: Authentication, @RequestParam name: String, @RequestBody file: MultipartFile?): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val originalFilename = file?.originalFilename ?: ""
        file?.uploadFile("public", name.replace(" ", "_").replace(Regex("[^A-Za-z0-9_]+"), "").uppercase() +
                "_${Random().nextLong().absoluteValue.toString(36).uppercase()}" +
                originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0)
        )

        return "redirect:/admin/control/upload-file?uploaded=ok"
    }

}
