package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class ManifestComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "manifest",
    "/",
    "Manifest",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            manifestGroup,
            name,
            shortName,
            description,
            display,
            applicationScope,
            startUrl,
            themeColor,
            backgroundColor,

            iconGroup,
            favicon,
            icon192,
            icon256,
            icon384,
            icon512,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val manifestGroup = SettingProxy(componentSettingService, component,
        "manifestGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "A manifest.json tartalma",
        description = "Ha nem tudod mi ez, inkább ne állítgasd!"
    )

    val name = SettingProxy(componentSettingService, component,
        "name", "Király Oldal", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Teljes név", description = ""
    )

    val shortName = SettingProxy(componentSettingService, component,
        "shortName", "Király Oldal", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Rövid név", description = ""
    )

    val description = SettingProxy(componentSettingService, component,
        "description", "", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Leírás", description = ""
    )

    val display = SettingProxy(componentSettingService, component,
        "display", "browser", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Kijelzés módja", description = "Csak ez lehet: browser, standalone, minimal-ui, fullscreen"
    )

    val applicationScope = SettingProxy(componentSettingService, component,
        "applicationScope", "/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Application scope", description = ""
    )

    val startUrl = SettingProxy(componentSettingService, component,
        "startUrl", "/", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Start url", description = ""
    )

    val themeColor = SettingProxy(componentSettingService, component,
        "themeColor", "#888888", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Téma színe", description = "Lehet különböző mint a stílus beállításokban"
    )

    val backgroundColor = SettingProxy(componentSettingService, component,
        "backgroundColor", "#000000", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Háttér színe", description = "Lehet különböző mint a stílus beállításokban"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val iconGroup = SettingProxy(componentSettingService, component,
        "iconGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Ikonok",
        description = "Generáld ki az oldal ikonjait például ennek az oldalnak a segítségével: " +
                "https://www.simicart.com/manifest-generator.html/"
    )

    val favicon = SettingProxy(componentSettingService, component,
        "favicon", "/cdn/manifest/favicon.ico",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Favicon", description = "16x16-os ico fájl"
    )

    val icon192 = SettingProxy(componentSettingService, component,
        "icon192", "/cdn/manifest/icon-192x192.png",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 192", description = "192x192 pixeles png fájl"
    )

    val icon256 = SettingProxy(componentSettingService, component,
        "icon256", "/cdn/manifest/icon-256x256.png",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 256", description = "256x256 pixeles png fájl"
    )

    val icon384 = SettingProxy(componentSettingService, component,
        "icon384", "/cdn/manifest/icon-384x384.png",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 384", description = "384x384 pixeles png fájl"
    )

    val icon512 = SettingProxy(componentSettingService, component,
        "icon512", "/cdn/manifest/icon-512x512.png",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 512", description = "512x512 pixeles png fájl"
    )

}
