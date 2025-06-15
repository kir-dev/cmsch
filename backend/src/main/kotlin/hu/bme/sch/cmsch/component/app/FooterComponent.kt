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
    componentSettingService,
    "footer",
    "/",
    "Lábléc",
    ControlPermissions.PERMISSION_CONTROL_FOOTER,
    listOf(),
    env
) {

    val footerGroup by SettingGroup(fieldName = "Lábléc")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(defaultValue = MinRoleSettingRef.ALL_ROLES,
        minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal"
    )

    var minimalisticFooter by BooleanSettingRef(fieldName = "Minimalisztikus lábléc",
        description = "Ha be van kapcsolva, akkor nem foglal olyan sok helyet a footer")

    var hostLogo by StringSettingRef(defaultValue = "https://warp.sch.bme.hu/img/blobs/redirect/",
        type = SettingType.URL, fieldName = "Esemény szervezőjének a logója", description = "A kép URL-je")

    var hostAlt by StringSettingRef(defaultValue = "Szervező kör", fieldName = "Esemény szervezőjének alt szövege",
        description = "Ha nem tölt be a kép ez jelenik meg")

    var hostWebsiteUrl by StringSettingRef(defaultValue = "https://kir-dev.sch.bme.hu/?ref=cmsch",
        type = SettingType.URL, fieldName = "Esemény szervezőjének oldala", description = "Az oldal url-je")

    var facebookUrl by StringSettingRef(type = SettingType.URL, fieldName = "Facebook url",
        description = "Ha üres, nem jelenik meg")

    var instagramUrl by StringSettingRef(type = SettingType.URL, fieldName = "Instagram url",
        description = "Ha üres, nem jelenik meg")

    var footerMessage by StringSettingRef(defaultValue = "email [at] sch.bme.hu\n2025", type = SettingType.LONG_TEXT,
        fieldName = "Footer szöveg", description = "Ez jelenik meg középen a footer alján")

    var devLogo by StringSettingRef(defaultValue = "https://warp.sch.bme.hu/img/blobs/redirect/",
        minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev logója", description = "A kép URL-je")

    var devAlt by StringSettingRef(defaultValue = "Kir-dev", minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "A kir-dev alt szövege", description = "Ha nem tölt be a kép ez jelenik meg")

    var devWebsiteUrl by StringSettingRef(defaultValue = "https://kir-dev.sch.bme.hu/?ref=cmsch",
        type = SettingType.URL, minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev oldala",
        description = "Az oldal url-je")

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorGroup by SettingGroup(fieldName = "Támogatók",
        description = "Az esemény támogatóinak logói (A lábléc felett jelenik meg)")

    var sponsorTitle by StringSettingRef(defaultValue = "Támogatóink", fieldName = "Szponzorok fejléc",
        description = "Ez a szöveg jelenik meg a szponzorok felett")

    var sponsorsEnabled by BooleanSettingRef(fieldName = "Sponsorok láthatóak")

    var sponsorLogoUrls by StringSettingRef(defaultValue = "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor logók", description = "URL-ek vesszővel (,) elválasztva")

    var sponsorAlts by StringSettingRef(defaultValue = "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor alt üzenetek", description = "Szövegek vesszővel (,) elválasztva")

    var sponsorWebsiteUrls by StringSettingRef(defaultValue = "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Sponsor weblapok", description = "URL-ek vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val partnerGroup by SettingGroup(fieldName = "Partnerek",
        description = "Az esemény partnerei (A lábléc felett jelenik meg)")

    var partnerTitle by StringSettingRef(defaultValue = "Partnereink", fieldName = "Szponzorok fejléc",
        description = "Ez a szöveg jelenik meg a szponzorok felett")

    var vikEnabled by BooleanSettingRef(fieldName = "BME VIK logó", description = "Legyen-e BME VIK logó a footerben")

    var bmeEnabled by BooleanSettingRef(fieldName = "BME logó", description = "Legyen-e BME logó a footerben")

    var schonherzEnabled by BooleanSettingRef(fieldName = "Schönherz logó",
        description = "Legyen-e Schönherz logó a footerben")

    var schdesignEnabled by BooleanSettingRef(fieldName = "schdesign logó",
        description = "Legyen-e schdesign logó a footerben")

    var partnerLogoUrls by StringSettingRef(defaultValue = "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner logók", description = "URL-ek vesszővel (,) elválasztva")

    var partnerAlts by StringSettingRef(defaultValue = "alt1,alt2", type = SettingType.LONG_TEXT,
        fieldName = "Partner alt üzenetek", description = "Szövegek vesszővel (,) elválasztva")

    var partnerWebsiteUrls by StringSettingRef(defaultValue = "url1,url2", type = SettingType.LONG_TEXT,
        fieldName = "Partner weblapok", description = "URL-ek vesszővel (,) elválasztva")

}
