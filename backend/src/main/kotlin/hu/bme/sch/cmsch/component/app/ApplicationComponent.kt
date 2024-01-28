package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

            siteGroup,
            siteName,
            defaultComponent,
        )
    }


    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val warningMessageGroup = SettingProxy(componentSettingService, component,
        "warningMessageGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Figyelmeztető üzenet",
        description = ""
    )

    val warningMessage = SettingProxy(componentSettingService, component,
        "warningMessage", "", type = SettingType.TEXT,
        fieldName = "Megjelenő üzenet"
    )

    val warningLevel = SettingProxy(componentSettingService, component,
        "warningLevel", "", type = SettingType.TEXT,
        fieldName = "Üzenet fontossági szintje", description = "lehet: success, info, warning, error"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val siteGroup = SettingProxy(componentSettingService, component,
        "siteGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Oldal beállítások",
        description = ""
    )

    val siteName = SettingProxy(componentSettingService, component,
        "siteName", "Király Esemény", type = SettingType.TEXT,
        fieldName = "Oldal neve", description = "Oldal vagy esemény neve"
    )

    val defaultComponent = SettingProxy(componentSettingService, component,
        "defaultComponent", "/home", type = SettingType.TEXT,
        fieldName = "Kezdő komponens", description = "Az a komponens ami kezdőlapként töltődik be"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val adminGroup = SettingProxy(componentSettingService, component,
        "adminGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Admin oldal beállításai",
        description = ""
    )

    val adminPanelName = SettingProxy(componentSettingService, component,
        "adminPanelName", "ADMIN", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Admin panel neve", description = "Az admin panel neve"
    )

    val isLive = SettingProxy(componentSettingService, component,
        "isLive", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Production oldal", description = "Ha be van kapcsolva akkor az oldal productionben van"
    )

    val siteUrl = SettingProxy(componentSettingService, component,
        "siteUrl", "http://127.0.0.1:3000/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Oldal URL-je", description = "Az elején van protokoll megnevezés és / jellel végződik"
    )

    val adminSiteUrl = SettingProxy(componentSettingService, component,
        "adminSiteUrl", "http://127.0.0.1:8080/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Admin Oldal URL-je", description = "Az elején van protokoll megnevezés és / jellel végződik"
    )

    val adminBrandColor = SettingProxy(componentSettingService, component,
        "adminBrandColor", "#00F460", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Admin menü színe", description = "Ez lesz az admin oldal színe"
    )

    val motd = SettingProxy(componentSettingService, component,
        "motd", "Message of the day", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "MOTD", description = "Ez jelenik meg belépés után"
    )

    val staffMessage = SettingProxy(componentSettingService, component,
        "staffMessage", "...", type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Szolgálati közlemény", description = "Ez fog megjelenni az admin oldal kezdőlapján"
    )

    override fun onPersist() {
        super.onPersist()
        if (adminBrandColor.getValue().isEmpty()) {
            adminBrandColor.setAndPersistValue(generateColor(adminSiteUrl.getValue()))
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
