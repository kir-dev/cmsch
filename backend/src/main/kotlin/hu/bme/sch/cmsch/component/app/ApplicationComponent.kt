package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

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
            motd,
            staffMessage,

            siteGroup,
            siteName,
            defaultComponent,

            footerGroup,
            minimalisticFooter,
            hostLogo,
            hostAlt,
            hostWebsiteUrl,
            facebookUrl,
            instagramUrl,
            footerMessage,
            devLogo,
            devAlt,
            devWebsiteUrl,

            sponsorGroup,
            sponsorsEnabled,
            sponsorLogoUrls,
            sponsorAlts,
            sponsorWebsiteUrls,

            debugGroup,
            submitDiff
        )
    }


    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
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
        fieldName = "Megjenelő üzenet"
    )

    val warningLevel = SettingProxy(componentSettingService, component,
        "warningLevel", "", type = SettingType.TEXT,
        fieldName = "Üzenet fontossági szintje", description = "lehet: success, info, warning, error"
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

    val motd = SettingProxy(componentSettingService, component,
        "motd", "Message of the day", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "MOTD", description = "Ez jelenik meg belépés után"
    )

    val staffMessage = SettingProxy(componentSettingService, component,
        "staffMessage", "...", type = SettingType.LONG_TEXT_MARKDOWN, serverSideOnly = true,
        fieldName = "Szolgálati közlemény", description = "Ez fog megjelenni az admin oldal kezdőlapján"
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

    val footerGroup = SettingProxy(componentSettingService, component,
        "footerGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Lábléc", description = ""
    )

    val minimalisticFooter = SettingProxy(componentSettingService, component,
        "minimalisticFooter", "false", type = SettingType.BOOLEAN,
        fieldName = "Minimalisztikus lábléc", description = "Ha be van kapcsolva, akkor nem foglal olyan sok helyet a footer"
    )

    val hostLogo = SettingProxy(componentSettingService, component,
        "hostLogo", "https://warp.sch.bme.hu/img/blobs/redirect/", type = SettingType.TEXT,
        fieldName = "Esemény szervezőjének a logója", description = "A kép URL-je"
    )

    val hostAlt = SettingProxy(componentSettingService, component,
        "hostAlt", "Szervező kör", type = SettingType.TEXT,
        fieldName = "Esemény szervezőjének alt szövege", description = "Ha nem tölt be a kép ez jelenik meg"
    )

    val hostWebsiteUrl = SettingProxy(componentSettingService, component,
        "hostWebsiteUrl", "https://kir-dev.sch.bme.hu/?ref=cmsch", type = SettingType.TEXT,
        fieldName = "Esemény szervezőjének oldala", description = "Az oldal url-je"
    )

    val facebookUrl = SettingProxy(componentSettingService, component,
        "facebookUrl", "", type = SettingType.TEXT,
        fieldName = "Facebook url", description = "Ha üres, nem jelenik meg"
    )

    val instagramUrl = SettingProxy(componentSettingService, component,
        "instagramUrl", "", type = SettingType.TEXT,
        fieldName = "Instagram url", description = "Ha üres, nem jelenik meg"
    )

    val footerMessage = SettingProxy(componentSettingService, component,
        "footerMessage", "email [at] sch.bme.hu\n2022", type = SettingType.LONG_TEXT,
        fieldName = "Footer szöveg", description = "Ez jelenik meg középpen a footer alján"
    )

    val devLogo = SettingProxy(componentSettingService, component,
        "devLogo", "https://warp.sch.bme.hu/img/blobs/redirect/",
        type = SettingType.TEXT, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "A kir-dev logója", description = "A kép URL-je"
    )

    val devAlt = SettingProxy(componentSettingService, component,
        "devAlt", "Kir-dev",
        type = SettingType.TEXT, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "A kir-dev alt szövege", description = "Ha nem tölt be a kép ez jelenik meg"
    )

    val devWebsiteUrl = SettingProxy(componentSettingService, component,
        "devWebsiteUrl", "https://kir-dev.sch.bme.hu/?ref=cmsch",
        type = SettingType.TEXT, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "A kir-dev oldala", description = "Az oldal url-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorGroup = SettingProxy(componentSettingService, component,
        "sponsorGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Támogatók", description = "Az esemény támogatóinak logói (A lábléc felett jelenik meg)"
    )

    val sponsorsEnabled = SettingProxy(componentSettingService, component,
        "sponsorsEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Sponsorok láthatóak"
    )

    val sponsorLogoUrls = SettingProxy(componentSettingService, component,
        "sponsorLogoUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor logók", description = "URL-ek vesszővel (,) elválasztva"
    )

    val sponsorAlts = SettingProxy(componentSettingService, component,
        "sponsorAlts", "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor alt üzenetek", description = "Szövegek vesszővel (,) elválasztva"
    )

    val sponsorWebsiteUrls = SettingProxy(componentSettingService, component,
        "sponsorWebsiteUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor weblapok", description = "URL-ek vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val debugGroup = SettingProxy(componentSettingService, component,
        "debugGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "DEBUG | Ehhez ne nyúlj", description = ""
    )

    val submitDiff = SettingProxy(componentSettingService, component,
        "submitDiff", "-7200", type = SettingType.NUMBER,
        fieldName = "Task beadás diff"
    )

}
