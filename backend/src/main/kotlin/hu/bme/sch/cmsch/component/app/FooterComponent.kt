package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class FooterComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "footer",
    "/",
    "Lábléc",
    ControlPermissions.PERMISSION_CONTROL_FOOTER,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
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
            sponsorTitle,
            sponsorsEnabled,
            sponsorLogoUrls,
            sponsorAlts,
            sponsorWebsiteUrls,

            partnerGroup,
            partnerTitle,
            vikEnabled,
            bmeEnabled,
            schonherzEnabled,
            schdesignEnabled,
            partnerLogoUrls,
            partnerAlts,
            partnerWebsiteUrls
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
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
        fieldName = "Footer szöveg", description = "Ez jelenik meg középen a footer alján"
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

    val sponsorTitle = SettingProxy(componentSettingService, component,
        "sponsorTitle", "Támogatóink", type = SettingType.TEXT,
        fieldName = "Szponzorok fejléc", description = "Ez a szöveg jelenik meg a szponzorok felett"
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

    val partnerGroup = SettingProxy(componentSettingService, component,
        "partnerGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Partnerek", description = "Az esemény partnerei (A lábléc felett jelenik meg)"
    )

    val partnerTitle = SettingProxy(componentSettingService, component,
        "partnerTitle", "Partnereink", type = SettingType.TEXT,
        fieldName = "Szponzorok fejléc", description = "Ez a szöveg jelenik meg a szponzorok felett"
    )

    val vikEnabled = SettingProxy(componentSettingService, component,
        "vikEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "BME VIK logó", description = "Legyen-e BME VIK logó a footerben"
    )

    val bmeEnabled = SettingProxy(componentSettingService, component,
        "bmeEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "BME logó", description = "Legyen-e BME logó a footerben"
    )

    val schonherzEnabled = SettingProxy(componentSettingService, component,
        "schonherzEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Schönherz logó", description = "Legyen-e Schönherz logó a footerben"
    )

    val schdesignEnabled = SettingProxy(componentSettingService, component,
        "schdesignEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "schdesign logó", description = "Legyen-e schdesign logó a footerben"
    )

    val partnerLogoUrls = SettingProxy(componentSettingService, component,
        "partnerLogoUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner logók", description = "URL-ek vesszővel (,) elválasztva"
    )

    val partnerAlts = SettingProxy(componentSettingService, component,
        "partnerAlts", "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Partner alt üzenetek", description = "Szövegek vesszővel (,) elválasztva"
    )

    val partnerWebsiteUrls = SettingProxy(componentSettingService, component,
        "partnerWebsiteUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner weblapok", description = "URL-ek vesszővel (,) elválasztva"
    )

}