package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.util.*
import jakarta.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/import")
@ConditionalOnBean(ApplicationComponent::class)
class ImportAdminController(
    private val adminMenuService: AdminMenuService,
    private val auditLogService: AuditLogService,
    private val components: List<ComponentBase>,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val view = "import"
    private val permissionControl = ControlPermissions.PERMISSION_CONTROL_APP_IMPORT

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            ApplicationComponent.DEVELOPER_CATEGORY, AdminMenuEntry(
                "Importálás",
                "file_upload",
                "/admin/control/${view}",
                31,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(
        model: Model,
        auth: Authentication,
        @RequestParam(defaultValue = "") count: String,
        @RequestParam(defaultValue = "") outOf: String
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            auditLogService.admin403(user, "import", "GET /import", permissionControl.permissionString)
            return "admin403"
        }

        model.addAttribute("user", user)
        model.addAttribute("importedCount", count.toIntOrNull())
        model.addAttribute("importedOutOf", outOf.toIntOrNull())
        model.addAttribute("permission", permissionControl.permissionString)

        return "fullImport"
    }

    @PostMapping("/file")
    fun importFromFile(@RequestParam("file") file: MultipartFile, auth: Authentication): String {
        val properties = Properties()
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            auditLogService.admin403(user, "import",
                "POST /import/file", permissionControl.permissionString)
            return "redirect:/admin/control/${view}"
        }

        return try {
            file.inputStream.use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    properties.load(reader)
                }
            }

            val propertiesMap = loadToMap(properties)
            val imported = importFromMap(propertiesMap, user)
            val action = "Imported $imported properties from (file) ${propertiesMap.size} entries"
            auditLogService.create(user, "import", action)
            log.info("{} {}", user.userName, action)
            "redirect:/admin/control/${view}?count=${imported}&outOf=${propertiesMap.size}"
        } catch (e: IOException) {
            log.error("Failed to import properties file by ${user.userName}", e)
            "redirect:/admin/control/${view}?count=0"
        }
    }

    @PostMapping("/string")
    fun importFromString(@RequestParam string: String, auth: Authentication): String {
        val properties = Properties()
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            auditLogService.admin403(user, "import",
                "POST /import/string", permissionControl.permissionString)
            return "redirect:/admin/control/${view}"
        }

        return try {
            properties.load(StringReader(string))
            val propertiesMap = loadToMap(properties)
            val imported = importFromMap(propertiesMap, user)
            val action = "Imported $imported properties from (string) ${propertiesMap.size} entries"
            auditLogService.create(user, "import", action)
            log.info("{} {}", user.userName, action)
            "redirect:/admin/control/${view}?count=${imported}&outOf=${propertiesMap.size}"
        } catch (e: IOException) {
            log.error("Failed to import properties string by ${user.userName}", e)
            "redirect:/admin/control/${view}?count=0"
        }
    }

    private fun loadToMap(properties: Properties): MutableMap<String, String> {
        val propertiesMap: MutableMap<String, String> = HashMap()
        for ((key, value) in properties) {
            propertiesMap[key.toString()] = value.toString()
        }
        return propertiesMap
    }

    private fun importFromMap(propertiesMap: MutableMap<String, String>, user: CmschUser): Int {
        var imported = 0
        components.forEach { component ->
            component.allSettings.forEach { setting ->
                propertiesMap["hu.bme.sch.cmsch.${component.component}.${setting.property}"]?.let {
                    setting.setAndPersistValue(it)
                    ++imported

                    val action = "set ${component.component}.${setting.property} to '${it}'"
                    log.info("{}: {}", user.userName, action)
                    auditLogService.edit(user, "import", action)
                }
            }
        }
        adminMenuService.invalidateSiteContext()
        return imported
    }

}
