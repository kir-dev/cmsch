package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.BooleanSettingRef
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.ControlGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingRef
import hu.bme.sch.cmsch.setting.SettingType
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class ApplicationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
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

    final override val allSettings by lazy {
        listOf(
            minRole,

            warningMessageGroup,
            warningMessage,
            warningLevel,

            adminGroup,
            adminPanelName,
            isLive,
            siteUrl,
            adminSiteUrl,
            adminBrandColor,
            motd,
            staffMessage,
            documentsForOrganizers,

            siteGroup,
            siteName,
            defaultComponent,
        )
    }


    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val warningMessageGroup = ControlGroup(component, "warningMessageGroup", fieldName = "Figyelmeztető üzenet")

    val warningMessage = StringSettingRef(componentSettingService, component,
        "warningMessage", "", type = SettingType.TEXT,
        fieldName = "Megjelenő üzenet"
    )

    val warningLevel = StringSettingRef(componentSettingService, component,
        "warningLevel", "", type = SettingType.TEXT,
        fieldName = "Üzenet fontossági szintje", description = "lehet: success, info, warning, error"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val siteGroup = ControlGroup(component, "siteGroup", fieldName = "Oldal beállítások")

    val siteName = StringSettingRef(componentSettingService, component,
        "siteName", "Király Esemény", type = SettingType.TEXT,
        fieldName = "Oldal neve", description = "Oldal vagy esemény neve"
    )

    val defaultComponent = StringSettingRef(componentSettingService, component,
        "defaultComponent", "/home", type = SettingType.TEXT,
        fieldName = "Kezdő komponens", description = "Az a komponens ami kezdőlapként töltődik be"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val adminGroup = ControlGroup(component, "adminGroup", fieldName = "Admin oldal beállításai")

    val adminPanelName = StringSettingRef(componentSettingService, component,
        "adminPanelName", "ADMIN", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Admin panel neve", description = "Az admin panel neve"
    )

    val isLive = BooleanSettingRef(componentSettingService, component,
        "isLive", false, serverSideOnly = true,
        fieldName = "Production oldal", description = "Ha be van kapcsolva akkor az oldal productionben van"
    )

    val siteUrl = StringSettingRef(componentSettingService, component,
        "siteUrl", "http://localhost:3000/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Oldal URL-je", description = "Az elején van protokoll megnevezés és / jellel végződik"
    )

    val adminSiteUrl = StringSettingRef(componentSettingService, component,
        "adminSiteUrl", "http://localhost:8080/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Admin Oldal URL-je", description = "Az elején van protokoll megnevezés és / jellel végződik"
    )

    val adminBrandColor = StringSettingRef(componentSettingService, component,
        "adminBrandColor", "#00F460", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Admin menü színe", description = "Ez lesz az admin oldal színe"
    )

    val motd = StringSettingRef(componentSettingService, component,
        "motd", "Message of the day", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "MOTD", description = "Ez jelenik meg belépés után"
    )

    val staffMessage = StringSettingRef(componentSettingService, component,
        "staffMessage", "...", type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Szolgálati közlemény", description = "Ez fog megjelenni az admin oldal kezdőlapján"
    )

    val documentsForOrganizers = StringSettingRef(componentSettingService, component,
        "documentsForOrganizers", "[]", type = SettingType.LONG_TEXT, serverSideOnly = true,
        fieldName = "Linkelt doksik", description = "Linkelt doksik az admin oldal kezdőlapján. Ikonok: sheets, docs, drive, calendar, forms, youtube, slides. Formátum: [{\"type\":\"sheets\",\"url\":\"https://xy\",\"title\":\"Title\",\"visible\":true}]"
    )

    override fun onPersist() {
        super.onPersist()
        if (adminBrandColor.getValue().isEmpty()) {
            adminBrandColor.setValue(generateColor(adminSiteUrl.getValue()))
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
