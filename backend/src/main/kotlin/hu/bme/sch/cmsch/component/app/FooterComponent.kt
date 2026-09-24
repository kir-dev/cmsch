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

    var hostLogo by StringSettingRef(defaultValue = "",
        type = SettingType.IMAGE_URL, fieldName = "Esemény szervezőjének a logója", description = "A kép URL-je")

    var hostAlt by StringSettingRef(defaultValue = "Szervező kör", fieldName = "Esemény szervezőjének alt szövege",
        description = "Ha nem tölt be a kép ez jelenik meg")

    var hostWebsiteUrl by StringSettingRef(defaultValue = "https://kir-dev.hu/project/cmsch",
        type = SettingType.URL, fieldName = "Esemény szervezőjének oldala", description = "Az oldal url-je")

    var facebookUrl by StringSettingRef(type = SettingType.URL, fieldName = "Facebook url",
        description = "Ha üres, nem jelenik meg")

    var instagramUrl by StringSettingRef(type = SettingType.URL, fieldName = "Instagram url",
        description = "Ha üres, nem jelenik meg")

    var footerMessage by StringSettingRef(defaultValue = "email [at] sch.bme.hu\n2025", type = SettingType.LONG_TEXT,
        fieldName = "Footer szöveg", description = "Ez jelenik meg középen a footer alján")

    var devWebsiteUrl by StringSettingRef(defaultValue = "https://kir-dev.hu/project/cmsch",
        type = SettingType.URL, minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev oldala",
        description = "Az oldal url-je")

    var bugReportURL by StringSettingRef(defaultValue = "https://kir-dev.hu/about/contact",
        type = SettingType.URL, minRoleToEdit = RoleType.SUPERUSER, fieldName = "A kir-dev kapcsolat linkje",
        description = "Kapcsolat link")

    /// -------------------------------------------------------------------------------------------------------------------

    val mainSupportersGroup by SettingGroup(fieldName = "Fő Támogatóink",
        description = "A fő támogatók logói a láblécben")

    var mainSupporterTitle by StringSettingRef(defaultValue = "Fő Támogatóink", fieldName = "Oszlop címe")

    var mainBmeEnabled by BooleanSettingRef(fieldName = "BME logó")

    var mainVikEnabled by BooleanSettingRef(fieldName = "BME VIK logó")

    var mainSchonherzEnabled by BooleanSettingRef(fieldName = "Schönherz logó")

    var mainSchdesignEnabled by BooleanSettingRef(fieldName = "schdesign logó")

    var mainSupporterLogoUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "További fő támogatói logók", description = "Kép URL-ek vesszővel (,) elválasztva; például MOL és PTC")

    var mainSupporterAlts by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Fő támogatói logók alt szövegei", description = "Szövegek vesszővel (,) elválasztva")

    var mainSupporterWebsiteUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Fő támogatók weboldalai", description = "URL-ek vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorGroup by SettingGroup(fieldName = "Kiemelt Támogatóink",
        description = "A kiemelt támogatók logói a láblécben")

    var sponsorTitle by StringSettingRef(defaultValue = "Kiemelt Támogatóink", fieldName = "Oszlop címe",
        description = "Ez a szöveg jelenik meg a logók felett")

    var featuredBmeEnabled by BooleanSettingRef(fieldName = "BME logó")

    var featuredVikEnabled by BooleanSettingRef(fieldName = "BME VIK logó")

    var featuredSchonherzEnabled by BooleanSettingRef(fieldName = "Schönherz logó")

    var featuredSchdesignEnabled by BooleanSettingRef(fieldName = "schdesign logó")

    var sponsorLogoUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "További kiemelt támogatói logók", description = "Kép URL-ek vesszővel (,) elválasztva")

    var sponsorAlts by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Kiemelt támogatói logók alt szövegei", description = "Szövegek vesszővel (,) elválasztva")

    var sponsorWebsiteUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Kiemelt támogatók weboldalai", description = "URL-ek vesszővel (,) elválasztva")

    /// -------------------------------------------------------------------------------------------------------------------

    val partnerGroup by SettingGroup(fieldName = "Többi Támogatónk",
        description = "A további támogatók logói a láblécben")

    var partnerTitle by StringSettingRef(defaultValue = "Többi Támogatónk", fieldName = "Oszlop címe",
        description = "Ez a szöveg jelenik meg a logók felett")

    var vikEnabled by BooleanSettingRef(fieldName = "BME VIK logó")

    var bmeEnabled by BooleanSettingRef(fieldName = "BME logó")

    var schonherzEnabled by BooleanSettingRef(fieldName = "Schönherz logó")

    var schdesignEnabled by BooleanSettingRef(fieldName = "schdesign logó")

    var partnerLogoUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "További támogatói logók", description = "Kép URL-ek vesszővel (,) elválasztva")

    var partnerAlts by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Támogatói logók alt szövegei", description = "Szövegek vesszővel (,) elválasztva")

    var partnerWebsiteUrls by StringSettingRef(defaultValue = "", type = SettingType.LONG_TEXT,
        fieldName = "Támogatók weboldalai", description = "URL-ek vesszővel (,) elválasztva")

}
