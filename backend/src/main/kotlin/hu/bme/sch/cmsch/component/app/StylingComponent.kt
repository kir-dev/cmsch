package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class StylingComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "style",
    "/",
    "Stílus",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            lightGroup,
            lightBackgroundColor,
            lightContainerColor,
            lightContainerFilter,
            lightNavbarColor,
            lightNavbarFilter,
            lightFooterBackground,
            lightFooterFilter,
            lightFooterShadowColor,
            lightTextColor,
            lightBrandingColor,
            lightBackgroundUrl,
            lightMobileBackgroundUrl,
            lightLogoUrl,

            darkGroup,
            darkModeEnabled,
            deviceTheme,
            forceDarkMode,
            darkBackgroundColor,
            darkContainerColor,
            darkContainerFilter,
            darkNavbarColor,
            darkNavbarFilter,
            darkFooterBackground,
            darkFooterFilter,
            darkFooterShadowColor,
            darkTextColor,
            darkBackgroundUrl,
            darkMobileBackgroundUrl,
            darkLogoUrl,

            typographyGroup,
            mainFontName,
            mainFontCdn,
            mainFontWeight,
            displayFontName,
            displayFontCdn,
            displayFontWeight,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val lightGroup = SettingProxy(componentSettingService, component,
        "lightGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Világos téma",
        description = "Az oldal világos stílusának színei, háttérképek"
    )

    val lightBackgroundColor = SettingProxy(componentSettingService, component,
        "lightBackgroundColor", "#FFFFFF", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik"
    )

    val lightContainerColor = SettingProxy(componentSettingService, component,
        "lightContainerColor", "transparent", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne"
    )

    val lightContainerFilter = SettingProxy(componentSettingService, component,
        "lightContainerFilter", "", type = SettingType.TEXT,
        fieldName = "A lapra alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightNavbarColor = SettingProxy(componentSettingService, component,
        "lightNavbarColor", "transparent", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne"
    )

    val lightNavbarFilter = SettingProxy(componentSettingService, component,
        "lightNavbarFilter", "", type = SettingType.TEXT,
        fieldName = "A navbarra alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightFooterBackground = SettingProxy(componentSettingService, component,
        "lightFooterBackground", "transparent", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne"
    )

    val lightFooterFilter = SettingProxy(componentSettingService, component,
        "lightFooterFilter", "", type = SettingType.TEXT,
        fieldName = "A footerre alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightFooterShadowColor = SettingProxy(componentSettingService, component,
        "lightFooterShadowColor", "#00000025", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe"
    )

    val lightTextColor = SettingProxy(componentSettingService, component,
        "lightTextColor", "#000000", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe"
    )

    val lightBrandingColor = SettingProxy(componentSettingService, component,
        "lightBrandingColor", "#880000", type = SettingType.COLOR,
        fieldName = "Brand szín", description = "Az oldal színes elemei ez alapján kerülnek kiszínezésre"
    )

    val lightBackgroundUrl = SettingProxy(componentSettingService, component,
        "lightBackgroundUrl", "", type = SettingType.URL,
        fieldName = "Háttérkép", description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val lightMobileBackgroundUrl = SettingProxy(componentSettingService, component,
        "lightMobileBackgroundUrl", "", type = SettingType.URL,
        fieldName = "Mobil háttérkép", description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val lightLogoUrl = SettingProxy(componentSettingService, component,
        "lightLogoUrl", "", type = SettingType.URL,
        fieldName = "Oldal logója", description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val darkGroup = SettingProxy(componentSettingService, component,
        "darkGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Sötét téma",
        description = "Az oldal sötét stílusának színei, háttérképek"
    )

    val darkModeEnabled = SettingProxy(componentSettingService, component,
        "darkModeEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Sötét téma elérhető", description = "Ha ez ki van kapcsolva, akkor nincs téma váltó"
    )

    val deviceTheme = SettingProxy(componentSettingService, component,
        "deviceTheme", "false", type = SettingType.BOOLEAN,
        fieldName = "Séma az eszköz alapján", description = "Ha be van kapcsolva, akkor lekéri, " +
                "hogy világos vagy sötét módban fut az eszköz. Csak akkor működik ha a sötét mód be van kapcsolva."
    )

    val forceDarkMode = SettingProxy(componentSettingService, component,
        "forceDarkMode", "false", type = SettingType.BOOLEAN,
        fieldName = "Csak a sötét téma érhető el", description = "Ha be van kapcsolva, akkor csak a sötét téma használható"
    )

    val darkBackgroundColor = SettingProxy(componentSettingService, component,
        "darkBackgroundColor", "#FFFFFF", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik"
    )

    val darkContainerColor = SettingProxy(componentSettingService, component,
        "darkContainerColor", "transparent", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne"
    )

    val darkContainerFilter = SettingProxy(componentSettingService, component,
        "darkContainerFilter", "", type = SettingType.TEXT,
        fieldName = "A lapra alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkNavbarColor = SettingProxy(componentSettingService, component,
        "darkNavbarColor", "transparent", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne"
    )

    val darkNavbarFilter = SettingProxy(componentSettingService, component,
        "darkNavbarFilter", "", type = SettingType.TEXT,
        fieldName = "A navbarra alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkFooterBackground = SettingProxy(componentSettingService, component,
        "darkFooterBackground", "transparent", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne"
    )

    val darkFooterFilter = SettingProxy(componentSettingService, component,
        "darkFooterFilter", "", type = SettingType.TEXT,
        fieldName = "A footerre alkalmazott filter", description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkFooterShadowColor = SettingProxy(componentSettingService, component,
        "darkFooterShadowColor", "#00000025", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe"
    )

    val darkTextColor = SettingProxy(componentSettingService, component,
        "darkTextColor", "#000000", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe"
    )

    val darkBackgroundUrl = SettingProxy(componentSettingService, component,
        "darkBackgroundUrl", "", type = SettingType.URL,
        fieldName = "Háttérkép", description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val darkMobileBackgroundUrl = SettingProxy(componentSettingService, component,
        "darkMobileBackgroundUrl", "", type = SettingType.URL,
        fieldName = "Mobil háttérkép", description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val darkLogoUrl = SettingProxy(componentSettingService, component,
        "darkLogoUrl", "", type = SettingType.URL,
        fieldName = "Oldal logója", description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val typographyGroup = SettingProxy(componentSettingService, component,
        "typographyGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tipográfia",
        description = "Betűtípusok nevei, elérhetősége és vastagsága"
    )

    val mainFontName = SettingProxy(componentSettingService, component,
        "mainFontName", "'Open Sans', sans-serif", type = SettingType.TEXT,
        fieldName = "Általános betűtípus", description = "Az általános (pl. szöveg blockokban megjelenő) betűtípus neve"
    )

    val mainFontCdn = SettingProxy(componentSettingService, component,
        "mainFontCdn", "https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;600&display=swap", type = SettingType.TEXT,
        fieldName = "Általános betűtípus CDN", description = "Az általános betűtípus CDN URL-je", serverSideOnly = true
    )

    val mainFontWeight = SettingProxy(componentSettingService, component,
        "mainFontWeight", "300", type = SettingType.NUMBER,
        fieldName = "Általános betűtípus vastagsága", description = "Az általános betűtípus vastagsága"
    )

    val displayFontName = SettingProxy(componentSettingService, component,
        "displayFontName", "'Bebas Neue', cursive", type = SettingType.TEXT,
        fieldName = "Kiemelt betűtípus", description = "A kiemelt (pl. fejlécként megjelenő) betűtípus neve. Ha üres akkor az általános lesz használva."
    )

    val displayFontCdn = SettingProxy(componentSettingService, component,
        "displayFontCdn", "https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap", type = SettingType.TEXT,
        fieldName = "Kiemelt betűtípus CDN", description = "A kiemelt betűtípus CDN URL-je", serverSideOnly = true
    )

    val displayFontWeight = SettingProxy(componentSettingService, component,
        "displayFontWeight", "400", type = SettingType.NUMBER,
        fieldName = "Kiemelt betűtípus vastagsága", description = "A kiemelt betűtípus vastagsága"
    )

}
