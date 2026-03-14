package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class StylingComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "style",
    "/",
    "Stílus",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val lightGroup by SettingGroup(fieldName = "Világos téma",
        description = "Az oldal világos stílusának színei, háttérképek")

    var lightBackgroundColor by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik")

    var lightContainerColor by StringSettingRef(defaultValue = "#f6f7f9", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne")

    var lightContainerFilter by StringSettingRef(fieldName = "A lapra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var lightNavbarColor by StringSettingRef(defaultValue = "#f6f7f9", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne")

    var lightNavbarFilter by StringSettingRef(fieldName = "A navbarra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var lightFooterBackground by StringSettingRef(defaultValue = "#f6f7f9", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne")

    var lightFooterFilter by StringSettingRef(fieldName = "A footerre alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var lightFooterShadowColor by StringSettingRef(defaultValue = "#00000005", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe")

    var lightTextColor by StringSettingRef(defaultValue = "#17181c", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe")

    var lightBrandingColor by StringSettingRef(defaultValue = "#3d50f5", type = SettingType.COLOR,
        fieldName = "Brand szín", description = "Az oldal színes elemei ez alapján kerülnek kiszínezésre")

    var lightPrimaryForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Brand előtér szín", description = "A brand színű elemeken megjelenő szöveg színe")

    var lightBorderColor by StringSettingRef(defaultValue = "#e2e8f0", type = SettingType.COLOR,
        fieldName = "Keret színe", description = "A keretek és elválasztók színe")

    var lightNeutralColor by StringSettingRef(defaultValue = "#edf2f7", type = SettingType.COLOR,
        fieldName = "Neutrális szín", description = "Másodlagos háttérszín (pl. kártyák, gombok)")

    var lightNeutralForeground by StringSettingRef(defaultValue = "#17181c", type = SettingType.COLOR,
        fieldName = "Neutrális előtér szín", description = "A neutrális színű elemeken megjelenő szöveg színe")

    var lightSuccessColor by StringSettingRef(defaultValue = "#48bb78", type = SettingType.COLOR,
        fieldName = "Siker szín", description = "Sikeres műveletek és állapotok színe")

    var lightSuccessForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Siker előtér szín", description = "A siker színű elemeken megjelenő szöveg színe")

    var lightWarningColor by StringSettingRef(defaultValue = "#ecc94b", type = SettingType.COLOR,
        fieldName = "Figyelmeztetés szín", description = "Figyelmeztetések és várakozási állapotok színe")

    var lightWarningForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Figyelmeztetés előtér szín", description = "A figyelmeztetés színű elemeken megjelenő szöveg színe")

    var lightDangerColor by StringSettingRef(defaultValue = "#f56565", type = SettingType.COLOR,
        fieldName = "Hiba szín", description = "Hibák és veszélyes műveletek színe")

    var lightDangerForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Hiba előtér szín", description = "A hiba színű elemeken megjelenő szöveg színe")

    var lightInfoColor by StringSettingRef(defaultValue = "#4299e1", type = SettingType.COLOR,
        fieldName = "Infó szín", description = "Információs üzenetek színe")

    var lightInfoForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Infó előtér szín", description = "Az infó színű elemeken megjelenő szöveg színe")

    var lightBackgroundUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Háttérkép",
        description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva.")

    var lightMobileBackgroundUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Mobil háttérkép",
        description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva.")

    var lightLogoUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Oldal logója",
        description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg.")

    /// -------------------------------------------------------------------------------------------------------------------

    val darkGroup by SettingGroup(fieldName = "Sötét téma",
        description = "Az oldal sötét stílusának színei, háttérképek")

    var darkModeEnabled by BooleanSettingRef(defaultValue = true, fieldName = "Sötét téma elérhető",
        description = "Ha ez ki van kapcsolva, akkor nincs téma váltó")

    var deviceTheme by BooleanSettingRef(defaultValue = true, fieldName = "Séma az eszköz alapján",
        description = "Ha be van kapcsolva, akkor lekéri, hogy világos vagy sötét módban fut az eszköz. Csak akkor működik ha a sötét mód be van kapcsolva.")

    var forceDarkMode by BooleanSettingRef(defaultValue = false, fieldName = "Csak a sötét téma érhető el",
        description = "Ha be van kapcsolva, akkor csak a sötét téma használható")

    var darkBackgroundColor by StringSettingRef(defaultValue = "#030712", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik")

    var darkContainerColor by StringSettingRef(defaultValue = "#0f141e", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne")

    var darkContainerFilter by StringSettingRef(fieldName = "A lapra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var darkNavbarColor by StringSettingRef(defaultValue = "#0f141e", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne")

    var darkNavbarFilter by StringSettingRef(fieldName = "A navbarra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var darkFooterBackground by StringSettingRef(defaultValue = "#0f141e", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne")

    var darkFooterFilter by StringSettingRef(fieldName = "A footerre alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)")

    var darkFooterShadowColor by StringSettingRef(defaultValue = "#00000025", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe")

    var darkTextColor by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe")

    var darkBrandingColor by StringSettingRef(defaultValue = "#3d50f5", type = SettingType.COLOR,
        fieldName = "Brand szín", description = "Az oldal színes elemei ez alapján kerülnek kiszínezésre")

    var darkPrimaryForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Brand előtér szín", description = "A brand színű elemeken megjelenő szöveg színe")

    var darkBorderColor by StringSettingRef(defaultValue = "#2d3748", type = SettingType.COLOR,
        fieldName = "Keret színe", description = "A keretek és elválasztók színe")

    var darkNeutralColor by StringSettingRef(defaultValue = "#2d3748", type = SettingType.COLOR,
        fieldName = "Neutrális szín", description = "Másodlagos háttérszín (pl. kártyák, gombok)")

    var darkNeutralForeground by StringSettingRef(defaultValue = "#ffffff", type = SettingType.COLOR,
        fieldName = "Neutrális előtér szín", description = "A neutrális színű elemeken megjelenő szöveg színe")

    var darkSuccessColor by StringSettingRef(defaultValue = "#68d391", type = SettingType.COLOR,
        fieldName = "Siker szín", description = "Sikeres műveletek és állapotok színe")

    var darkSuccessForeground by StringSettingRef(defaultValue = "#1a202c", type = SettingType.COLOR,
        fieldName = "Siker előtér szín", description = "A siker színű elemeken megjelenő szöveg színe")

    var darkWarningColor by StringSettingRef(defaultValue = "#faf089", type = SettingType.COLOR,
        fieldName = "Figyelmeztetés szín", description = "Figyelmeztetések és várakozási állapotok színe")

    var darkWarningForeground by StringSettingRef(defaultValue = "#1a202c", type = SettingType.COLOR,
        fieldName = "Figyelmeztetés előtér szín", description = "A figyelmeztetés színű elemeken megjelenő szöveg színe")

    var darkDangerColor by StringSettingRef(defaultValue = "#fc8181", type = SettingType.COLOR,
        fieldName = "Hiba szín", description = "Hibák és veszélyes műveletek színe")

    var darkDangerForeground by StringSettingRef(defaultValue = "#1a202c", type = SettingType.COLOR,
        fieldName = "Hiba előtér szín", description = "A hiba színű elemeken megjelenő szöveg színe")

    var darkInfoColor by StringSettingRef(defaultValue = "#63b3ed", type = SettingType.COLOR,
        fieldName = "Infó szín", description = "Információs üzenetek színe")

    var darkInfoForeground by StringSettingRef(defaultValue = "#1a202c", type = SettingType.COLOR,
        fieldName = "Infó előtér szín", description = "Az infó színű elemeken megjelenő szöveg színe")

    var darkBackgroundUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Háttérkép",
        description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva.")

    var darkMobileBackgroundUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Mobil háttérkép",
        description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva.")

    var darkLogoUrl by StringSettingRef(type = SettingType.IMAGE_URL, fieldName = "Oldal logója",
        description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg.")

    /// -------------------------------------------------------------------------------------------------------------------

    val typographyGroup by SettingGroup(fieldName = "Tipográfia",
        description = "Betűtípusok nevei, elérhetősége és vastagsága")

    var mainFontName by StringSettingRef(defaultValue = "'Open Sans', sans-serif", fieldName = "Általános betűtípus",
        description = "Az általános (pl. szöveg blockokban megjelenő) betűtípus neve")

    var mainFontCdn by StringSettingRef(defaultValue = "https://fonts.googleapis.com/css2?family=Open+Sans&display=swap",
        type = SettingType.URL, fieldName = "Általános betűtípus CDN",
        description = "Az általános betűtípus CDN URL-je", serverSideOnly = true)

    var displayFontName by StringSettingRef(defaultValue = "'Open Sans', sans-serif", fieldName = "Kiemelt betűtípus",
        description = "A kiemelt (pl. fejlécként megjelenő) betűtípus neve. Ha üres akkor az általános lesz használva.")

    var displayFontCdn by StringSettingRef(defaultValue = "https://fonts.googleapis.com/css2?family=Open+Sans&display=swap",
        type = SettingType.URL, fieldName = "Kiemelt betűtípus CDN",
        description = "A kiemelt betűtípus CDN URL-je", serverSideOnly = true)

}
