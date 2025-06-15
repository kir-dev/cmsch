package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class ApplicationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "app",
    "/app",
    "Alkalmazás",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(ExtraMenuEntity::class),
    env
) {

    companion object {
        const val STYLING_CATEGORY = "ApplicationComponent.style"
        const val CONTENT_CATEGORY = "ApplicationComponent.content"
        const val FUNCTIONALITIES_CATEGORY = "ApplicationComponent.function"
        const val DEVELOPER_CATEGORY = "ApplicationComponent.dev"
        const val DATA_SOURCE_CATEGORY = "ApplicationComponent.data"
    }

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", minRoleToEdit = RoleType.NOBODY,
        description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val warningMessageGroup by SettingGroup(fieldName = "Figyelmeztető üzenet")

    var warningMessage by StringSettingRef(fieldName = "Megjelenő üzenet")

    var warningLevel by StringSettingRef(fieldName = "Üzenet fontossági szintje",
        description = "lehet: success, info, warning, error")

    /// -------------------------------------------------------------------------------------------------------------------

    val siteGroup by SettingGroup(fieldName = "Oldal beállítások")

    var siteName by StringSettingRef(defaultValue = "Király Esemény", fieldName = "Oldal neve",
        description = "Oldal vagy esemény neve")

    var defaultComponent by StringSettingRef(defaultValue = "/home", fieldName = "Kezdő komponens",
        description = "Az a komponens ami kezdőlapként töltődik be")

    /// -------------------------------------------------------------------------------------------------------------------

    val adminGroup by SettingGroup(fieldName = "Admin oldal beállításai")

    var adminPanelName by StringSettingRef(defaultValue = "ADMIN", fieldName = "Admin panel neve",
        description = "Az admin panel neve", serverSideOnly = true)

    var isLive by BooleanSettingRef(fieldName = "Production oldal",
        description = "Ha be van kapcsolva akkor az oldal productionben van", serverSideOnly = true)

    var siteUrl by StringSettingRef(defaultValue = "http://localhost:3000/",
        fieldName = "Oldal URL-je",
        description = "Az elején van protokoll megnevezés és / jellel végződik", serverSideOnly = true)

    var adminSiteUrl by StringSettingRef(defaultValue = "http://localhost:8080/",
        fieldName = "Admin Oldal URL-je",
        description = "Az elején van protokoll megnevezés és / jellel végződik", serverSideOnly = true)

    var adminBrandColor by StringSettingRef(
        defaultValue = "#00F460", type = SettingType.COLOR,
        fieldName = "Admin menü színe", description = "Ez lesz az admin oldal színe", serverSideOnly = true,
    )

    var motd by StringSettingRef(defaultValue = "Message of the day", fieldName = "MOTD",
        description = "Ez jelenik meg belépés után", serverSideOnly = true)

    var staffMessage by StringSettingRef(defaultValue = "...", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Szolgálati közlemény",
        description = "Ez fog megjelenni az admin oldal kezdőlapján", serverSideOnly = true)

    var documentsForOrganizers by StringSettingRef(defaultValue = "[]", type = SettingType.LONG_TEXT,
        fieldName = "Linkelt doksik", serverSideOnly = true,
        description = "Linkelt doksik az admin oldal kezdőlapján. Ikonok: sheets, docs, drive, calendar, forms, youtube, slides. Formátum: [{\"type\":\"sheets\",\"url\":\"https://xy\",\"title\":\"Title\",\"visible\":true}]")

    override fun onPersist() {
        super.onPersist()
        if (adminBrandColor.isEmpty()) {
            adminBrandColor = generateColor(adminSiteUrl)
        }
    }

    fun generateColor(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())

        var red = bytes[0].toInt() and 0xFF
        var green = bytes[1].toInt() and 0xFF
        var blue = bytes[2].toInt() and 0xFF

        red = (red % 128) + 64
        green = (green % 128) + 64
        blue = (blue % 128) + 64

        return String.format("#%02X%02X%02X", red, green, blue)
    }


}
