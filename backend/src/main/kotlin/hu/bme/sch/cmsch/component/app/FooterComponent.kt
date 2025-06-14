package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
) {

    final override val allSettings by lazy {
        listOf(
            footerGroup,
            minRole,
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

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val footerGroup = SettingGroup(component, "footerGroup", fieldName = "Lábléc")

    val minimalisticFooter = BooleanSettingRef(componentSettingService, component,
        "minimalisticFooter", false, fieldName = "Minimalisztikus lábléc",
        description = "Ha be van kapcsolva, akkor nem foglal olyan sok helyet a footer"
    )

    val hostLogo = StringSettingRef(componentSettingService, component,
        "hostLogo", "https://warp.sch.bme.hu/img/blobs/redirect/", type = SettingType.URL,
        fieldName = "Esemény szervezőjének a logója", description = "A kép URL-je"
    )

    val hostAlt = StringSettingRef(componentSettingService, component,
        "hostAlt", "Szervező kör", fieldName = "Esemény szervezőjének alt szövege",
        description = "Ha nem tölt be a kép ez jelenik meg"
    )

    val hostWebsiteUrl = StringSettingRef(componentSettingService, component,
        "hostWebsiteUrl", "https://kir-dev.sch.bme.hu/?ref=cmsch", type = SettingType.URL,
        fieldName = "Esemény szervezőjének oldala", description = "Az oldal url-je"
    )

    val facebookUrl = StringSettingRef(componentSettingService, component,
        "facebookUrl", "", type = SettingType.URL,
        fieldName = "Facebook url", description = "Ha üres, nem jelenik meg"
    )

    val instagramUrl = StringSettingRef(componentSettingService, component,
        "instagramUrl", "", type = SettingType.URL,
        fieldName = "Instagram url", description = "Ha üres, nem jelenik meg"
    )

    val footerMessage = StringSettingRef(componentSettingService, component,
        "footerMessage", "email [at] sch.bme.hu\n2025", type = SettingType.LONG_TEXT,
        fieldName = "Footer szöveg", description = "Ez jelenik meg középen a footer alján"
    )

    val devLogo = StringSettingRef(componentSettingService, component,
        "devLogo", "https://warp.sch.bme.hu/img/blobs/redirect/",
        minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev logója", description = "A kép URL-je"
    )

    val devAlt = StringSettingRef(componentSettingService, component,
        "devAlt", "Kir-dev", minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "A kir-dev alt szövege", description = "Ha nem tölt be a kép ez jelenik meg"
    )

    val devWebsiteUrl = StringSettingRef(componentSettingService, component,
        "devWebsiteUrl", "https://kir-dev.sch.bme.hu/?ref=cmsch", type = SettingType.URL,
        minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev oldala", description = "Az oldal url-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorGroup = SettingGroup(component, "sponsorGroup", fieldName = "Támogatók",
        description = "Az esemény támogatóinak logói (A lábléc felett jelenik meg)"
    )

    val sponsorTitle = StringSettingRef(componentSettingService, component,
        "sponsorTitle", "Támogatóink", fieldName = "Szponzorok fejléc",
        description = "Ez a szöveg jelenik meg a szponzorok felett"
    )

    val sponsorsEnabled = BooleanSettingRef(componentSettingService, component,
        "sponsorsEnabled", false, fieldName = "Sponsorok láthatóak"
    )

    val sponsorLogoUrls = StringSettingRef(componentSettingService, component,
        "sponsorLogoUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor logók", description = "URL-ek vesszővel (,) elválasztva"
    )

    val sponsorAlts = StringSettingRef(componentSettingService, component,
        "sponsorAlts", "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor alt üzenetek", description = "Szövegek vesszővel (,) elválasztva"
    )

    val sponsorWebsiteUrls = StringSettingRef(componentSettingService, component,
        "sponsorWebsiteUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor weblapok", description = "URL-ek vesszővel (,) elválasztva"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val partnerGroup = SettingGroup(component, "partnerGroup", fieldName = "Partnerek",
        description = "Az esemény partnerei (A lábléc felett jelenik meg)"
    )

    val partnerTitle = StringSettingRef(componentSettingService, component,
        "partnerTitle", "Partnereink", fieldName = "Szponzorok fejléc",
        description = "Ez a szöveg jelenik meg a szponzorok felett"
    )

    val vikEnabled = BooleanSettingRef(componentSettingService, component,
        "vikEnabled", false, fieldName = "BME VIK logó", description = "Legyen-e BME VIK logó a footerben"
    )

    val bmeEnabled = BooleanSettingRef(componentSettingService, component,
        "bmeEnabled", false, fieldName = "BME logó", description = "Legyen-e BME logó a footerben"
    )

    val schonherzEnabled = BooleanSettingRef(componentSettingService, component,
        "schonherzEnabled", false, fieldName = "Schönherz logó", description = "Legyen-e Schönherz logó a footerben"
    )

    val schdesignEnabled = BooleanSettingRef(componentSettingService, component,
        "schdesignEnabled", false, fieldName = "schdesign logó", description = "Legyen-e schdesign logó a footerben"
    )

    val partnerLogoUrls = StringSettingRef(componentSettingService, component,
        "partnerLogoUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner logók", description = "URL-ek vesszővel (,) elválasztva"
    )

    val partnerAlts = StringSettingRef(componentSettingService, component,
        "partnerAlts", "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Partner alt üzenetek", description = "Szövegek vesszővel (,) elválasztva"
    )

    val partnerWebsiteUrls = StringSettingRef(componentSettingService, component,
        "partnerWebsiteUrls", "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner weblapok", description = "URL-ek vesszővel (,) elválasztva"
    )

}
