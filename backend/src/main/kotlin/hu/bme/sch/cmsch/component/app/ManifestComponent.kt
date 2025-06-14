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
    componentSettingService,
    "manifest",
    "/",
    "Manifest",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val manifestGroup by SettingGroup(fieldName = "A manifest.json tartalma",
        description = "Ha nem tudod mi ez, inkább ne állítgasd!"
    )

    var name by StringSettingRef("Király Oldal", serverSideOnly = true, fieldName = "Teljes név")

    var shortName by StringSettingRef("Király Oldal", serverSideOnly = true, fieldName = "Rövid név")

    var description by StringSettingRef("", serverSideOnly = true, fieldName = "Leírás")

    var display by StringSettingRef("browser", serverSideOnly = true, fieldName = "Kijelzés módja",
        description = "Csak ez lehet: browser, standalone, minimal-ui, fullscreen")

    var applicationScope by StringSettingRef("/", serverSideOnly = true, fieldName = "Application scope")

    var startUrl by StringSettingRef("/", type = SettingType.URL, serverSideOnly = true, fieldName = "Start url")

    var themeColor by StringSettingRef("#888888", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Téma színe", description = "Lehet különböző mint a stílus beállításokban")

    var backgroundColor by StringSettingRef("#000000", type = SettingType.COLOR, serverSideOnly = true,
        fieldName = "Háttér színe", description = "Lehet különböző mint a stílus beállításokban")

    /// -------------------------------------------------------------------------------------------------------------------

    val iconGroup by SettingGroup(fieldName = "Ikonok",
        description = "Generáld ki az oldal ikonjait például ennek az oldalnak a segítségével: https://www.simicart.com/manifest-generator.html/"
    )

    var favicon by StringSettingRef("manifest/favicon.ico",
        type = SettingType.IMAGE,
        serverSideOnly = true,
        persist = false,
        fieldName = "Favicon",
        description = "16x16-os ico fájl"
    )

    var icon192 by StringSettingRef("manifest/icon-192x192.png",
        type = SettingType.IMAGE,
        serverSideOnly = true,
        persist = false,
        fieldName = "Ikon 192",
        description = "192x192 pixeles png fájl"
    )

    var icon256 by StringSettingRef("manifest/icon-256x256.png",
        type = SettingType.IMAGE,
        serverSideOnly = true,
        persist = false,
        fieldName = "Ikon 256",
        description = "256x256 pixeles png fájl"
    )

    var icon384 by StringSettingRef("manifest/icon-384x384.png",
        type = SettingType.IMAGE,
        serverSideOnly = true,
        persist = false,
        fieldName = "Ikon 384",
        description = "384x384 pixeles png fájl"
    )

    var icon512 by StringSettingRef("manifest/icon-512x512.png",
        type = SettingType.IMAGE, serverSideOnly = true, persist = false,
        fieldName = "Ikon 512",
        description = "512x512 pixeles png fájl"
    )

}
