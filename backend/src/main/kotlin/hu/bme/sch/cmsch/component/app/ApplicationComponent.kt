package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class ApplicationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("app", "/app", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            warningMessageGroup,
            warningMessage,
            warningLevel,

            adminGroup,
            siteUrl,
            adminSiteUrl,
            motd,
            staffMessage,

            siteGroup,
            siteName,
            defaultComponent,
            siteLogoUrl,
            faviconUrl,

            footerGroup,
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
        "staffMessage", "http://127.0.0.1:8080/", type = SettingType.LONG_TEXT, serverSideOnly = true,
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
        "defaultComponent", "home", type = SettingType.COMPONENT_NAME,
        fieldName = "Kezdő komponens", description = "Az a komponens ami kezdőlapként töltődik be"
    )

    val siteLogoUrl = SettingProxy(componentSettingService, component,
        "siteLogoUrl", "https://", type = SettingType.TEXT,
        fieldName = "Oldal logója", description = "Oldal vagy esemény logójának url-je"
    )

    val faviconUrl = SettingProxy(componentSettingService, component,
        "faviconUrl", "https://", type = SettingType.TEXT,
        fieldName = "Oldal ikonja", description = "Oldal ikonjának url-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val footerGroup = SettingProxy(componentSettingService, component,
        "footerGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Lábléc", description = ""
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

}
