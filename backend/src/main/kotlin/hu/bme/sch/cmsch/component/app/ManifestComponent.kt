package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
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

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val manifestGroup = SettingGroup(component, "manifestGroup", fieldName = "A manifest.json tartalma",
        description = "Ha nem tudod mi ez, inkább ne állítgasd!"
    )

    val name = StringSettingRef(componentSettingService, component,
        "name", "Király Oldal", serverSideOnly = true, fieldName = "Teljes név"
    )

    val shortName = StringSettingRef(componentSettingService, component,
        "shortName", "Király Oldal", serverSideOnly = true, fieldName = "Rövid név"
    )

    val description = StringSettingRef(componentSettingService, component,
        "description", "", serverSideOnly = true, fieldName = "Leírás"
    )

    val display = StringSettingRef(componentSettingService, component,
        "display", "browser", serverSideOnly = true, fieldName = "Kijelzés módja",
        description = "Csak ez lehet: browser, standalone, minimal-ui, fullscreen"
    )

    val applicationScope = StringSettingRef(componentSettingService, component,
        "applicationScope", "/", serverSideOnly = true, fieldName = "Application scope"
    )

    val startUrl = StringSettingRef(componentSettingService, component,
        "startUrl", "/", type = SettingType.URL, serverSideOnly = true, fieldName = "Start url"
    )

    val themeColor = StringSettingRef(componentSettingService, component,
        "themeColor", "#888888", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Téma színe", description = "Lehet különböző mint a stílus beállításokban"
    )

    val backgroundColor = StringSettingRef(componentSettingService, component,
        "backgroundColor", "#000000", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Háttér színe", description = "Lehet különböző mint a stílus beállításokban"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val iconGroup = SettingGroup(component, "iconGroup", fieldName = "Ikonok",
        description = "Generáld ki az oldal ikonjait például ennek az oldalnak a segítségével: https://www.simicart.com/manifest-generator.html/"
    )

    val favicon = StringSettingRef(componentSettingService, component,
        "favicon", "manifest/favicon.ico", type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Favicon", description = "16x16-os ico fájl"
    )

    val icon192 = StringSettingRef(componentSettingService, component,
        "icon192", "manifest/icon-192x192.png", type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 192", description = "192x192 pixeles png fájl"
    )

    val icon256 = StringSettingRef(componentSettingService, component,
        "icon256", "manifest/icon-256x256.png", type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 256", description = "256x256 pixeles png fájl"
    )

    val icon384 = StringSettingRef(componentSettingService, component,
        "icon384", "manifest/icon-384x384.png", type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 384", description = "384x384 pixeles png fájl"
    )

    val icon512 = StringSettingRef(componentSettingService, component,
        "icon512", "manifest/icon-512x512.png", type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 512", description = "512x512 pixeles png fájl"
    )

}
