package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.component.app.ApplicationComponent
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.optimizeImage
import hu.bme.sch.cmsch.util.resizeImage
import hu.bme.sch.cmsch.util.urlEncode
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import javax.imageio.ImageIO
import kotlin.uuid.Uuid

@Controller
@RequestMapping("/admin/control/gallery-upload")
@ConditionalOnBean(GalleryComponent::class)
class GalleryUploadController(
    private val adminMenuService: AdminMenuService,
    private val applicationComponent: ApplicationComponent,
    private val auditLog: AuditLogService,
    private val storageService: StorageService,
    private val galleryService: GalleryService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val permissionControl = StaffPermissions.PERMISSION_CREATE_GALLERY

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            GalleryComponent::class.simpleName!!, AdminMenuEntry(
                "Képfeltöltés",
                "cloud_upload",
                "/admin/control/gallery-upload",
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
            auditLog.admin403(user, "gallery-upload", "GET /gallery-upload", permissionControl.permissionString)
            return "admin403"
        }

        model.addAttribute("user", user)
        model.addAttribute("uploaded", uploaded)
        model.addAttribute("baseUrl", applicationComponent.adminSiteUrl)
        model.addAttribute("permission", permissionControl.permissionString)

        return "uploadGallery"
    }

    @PostMapping("")
    fun uploadImage(
        model: Model,
        auth: Authentication,
        @RequestParam names: List<String>,
        @RequestParam descriptions: List<String>,
        @RequestParam(defaultValue = "false") highlighted: List<Boolean>,
        @RequestParam(defaultValue = "false") showOnHomePage: List<Boolean>,
        @RequestParam files: List<MultipartFile>
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, "gallery-upload", "POST /gallery-upload", permissionControl.permissionString)
            return "admin403"
        }

        if (names.size != files.size) {
            throw IllegalArgumentException("The length of names and files does not match")
        }

        val uploadedPhotos = mutableListOf<String>()
        for (i in files.indices) {
            val file = files[i]
            val name = names[i]
            val description = descriptions.getOrNull(i) ?: ""
            val isHighlighted = highlighted.getOrNull(i) ?: false
            val isShowOnHomePage = showOnHomePage.getOrNull(i) ?: false

            val randomId = Uuid.generateV7().toString()
            val cleanName = name.replace(" ", "_").replace(Regex("[^A-Za-z0-9_]+"), "").uppercase()
            val extension = file.originalFilename?.substringAfterLast(".") ?: "jpg"
            val baseName = if (cleanName.isBlank()) randomId else "${cleanName}_$randomId"

            val fileName = "${baseName}.$extension"
            val thumbName = "${baseName}_thumb.$extension"

            storageService.saveNamedObject("gallery", fileName, file).ifPresent { url ->
                val thumbUrl = generateThumbnail(file)?.let { thumbData ->
                    storageService.saveNamedObject("gallery", thumbName, "image/jpeg", thumbData)
                        .orElse("")
                } ?: ""

                val entity = GalleryEntity(
                    title = name,
                    description = description,
                    highlighted = isHighlighted,
                    showOnHomePage = isShowOnHomePage,
                    url = url,
                    thumbnailUrl = thumbUrl
                )
                galleryService.savePhoto(entity)
                uploadedPhotos.add(name)
            }
        }

        return "redirect:/admin/control/gallery-upload?uploaded=${uploadedPhotos.joinToString(",").urlEncode()}"
    }

    private fun generateThumbnail(file: MultipartFile): ByteArray? {
        return try {
            ImageIO.read(file.inputStream)
                ?.resizeImage(800, 800)
                ?.optimizeImage("jpg")
        } catch (e: Exception) {
            log.error("Failed to optimize image", e)
            return null
        }
    }

}
