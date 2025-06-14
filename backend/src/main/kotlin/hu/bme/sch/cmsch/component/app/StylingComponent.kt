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

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val lightGroup = SettingGroup(component, "lightGroup", fieldName = "Világos téma",
        description = "Az oldal világos stílusának színei, háttérképek"
    )

    val lightBackgroundColor = StringSettingRef(componentSettingService, component,
        "lightBackgroundColor", "#FFFFFF", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik"
    )

    val lightContainerColor = StringSettingRef(componentSettingService, component,
        "lightContainerColor", "transparent", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne"
    )

    val lightContainerFilter = StringSettingRef(componentSettingService, component,
        "lightContainerFilter", "", fieldName = "A lapra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightNavbarColor = StringSettingRef(componentSettingService, component,
        "lightNavbarColor", "transparent", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne"
    )

    val lightNavbarFilter = StringSettingRef(componentSettingService, component,
        "lightNavbarFilter", "", fieldName = "A navbarra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightFooterBackground = StringSettingRef(componentSettingService, component,
        "lightFooterBackground", "transparent", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne"
    )

    val lightFooterFilter = StringSettingRef(componentSettingService, component,
        "lightFooterFilter", "", fieldName = "A footerre alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val lightFooterShadowColor = StringSettingRef(componentSettingService, component,
        "lightFooterShadowColor", "#00000025", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe"
    )

    val lightTextColor = StringSettingRef(componentSettingService, component,
        "lightTextColor", "#000000", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe"
    )

    val lightBrandingColor = StringSettingRef(componentSettingService, component,
        "lightBrandingColor", "#880000", type = SettingType.COLOR,
        fieldName = "Brand szín", description = "Az oldal színes elemei ez alapján kerülnek kiszínezésre"
    )

    val lightBackgroundUrl = StringSettingRef(componentSettingService,
        component, "lightBackgroundUrl", "", type = SettingType.URL, fieldName = "Háttérkép",
        description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val lightMobileBackgroundUrl = StringSettingRef(componentSettingService,
        component, "lightMobileBackgroundUrl", "", type = SettingType.URL, fieldName = "Mobil háttérkép",
        description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val lightLogoUrl = StringSettingRef(componentSettingService,
        component, "lightLogoUrl", "", type = SettingType.URL, fieldName = "Oldal logója",
        description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val darkGroup = SettingGroup(component, "darkGroup", fieldName = "Sötét téma",
        description = "Az oldal sötét stílusának színei, háttérképek"
    )

    val darkModeEnabled = BooleanSettingRef(componentSettingService, component,
        "darkModeEnabled", true, fieldName = "Sötét téma elérhető",
        description = "Ha ez ki van kapcsolva, akkor nincs téma váltó"
    )

    val deviceTheme = BooleanSettingRef(componentSettingService, component,
        "deviceTheme", false, fieldName = "Séma az eszköz alapján",
        description = "Ha be van kapcsolva, akkor lekéri, hogy világos vagy sötét módban fut az eszköz. Csak akkor működik ha a sötét mód be van kapcsolva."
    )

    val forceDarkMode = BooleanSettingRef(componentSettingService,
        component, "forceDarkMode", false, fieldName = "Csak a sötét téma érhető el",
        description = "Ha be van kapcsolva, akkor csak a sötét téma használható"
    )

    val darkBackgroundColor = StringSettingRef(componentSettingService, component,
        "darkBackgroundColor", "#FFFFFF", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik"
    )

    val darkContainerColor = StringSettingRef(componentSettingService, component,
        "darkContainerColor", "transparent", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne"
    )

    val darkContainerFilter = StringSettingRef(componentSettingService, component,
        "darkContainerFilter", "", fieldName = "A lapra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkNavbarColor = StringSettingRef(componentSettingService, component,
        "darkNavbarColor", "transparent", type = SettingType.COLOR,
        fieldName = "Navbar színe", description = "A Navbar háttérszíne"
    )

    val darkNavbarFilter = StringSettingRef(componentSettingService, component,
        "darkNavbarFilter", "", fieldName = "A navbarra alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkFooterBackground = StringSettingRef(componentSettingService, component,
        "darkFooterBackground", "transparent", type = SettingType.COLOR,
        fieldName = "Footer színe", description = "A Footer háttérszíne"
    )

    val darkFooterFilter = StringSettingRef(componentSettingService, component,
        "darkFooterFilter", "", fieldName = "A footerre alkalmazott filter",
        description = "CSS backdrop-filter, például blur(8px)"
    )

    val darkFooterShadowColor = StringSettingRef(componentSettingService, component,
        "darkFooterShadowColor", "#00000025", type = SettingType.COLOR,
        fieldName = "Footer alsó sáv színe", description = "Footer legalsó sávjának a színe"
    )

    val darkTextColor = StringSettingRef(componentSettingService, component,
        "darkTextColor", "#000000", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe"
    )

    val darkBackgroundUrl = StringSettingRef(componentSettingService, component,
        "darkBackgroundUrl", "", type = SettingType.URL, fieldName = "Háttérkép",
        description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val darkMobileBackgroundUrl = StringSettingRef(componentSettingService, component,
        "darkMobileBackgroundUrl", "", type = SettingType.URL, fieldName = "Mobil háttérkép",
        description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val darkLogoUrl = StringSettingRef(componentSettingService, component,
        "darkLogoUrl", "", type = SettingType.URL, fieldName = "Oldal logója",
        description = "Oldalon megjelenő logó URL-je. Ha üres, akkor az oldal neve jelenik meg."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val typographyGroup = SettingGroup(component, "typographyGroup", fieldName = "Tipográfia",
        description = "Betűtípusok nevei, elérhetősége és vastagsága"
    )

    val mainFontName = StringSettingRef(componentSettingService, component,
        "mainFontName", "'Open Sans', sans-serif", fieldName = "Általános betűtípus",
        description = "Az általános (pl. szöveg blockokban megjelenő) betűtípus neve"
    )

    val mainFontCdn = StringSettingRef(componentSettingService, component,
        "mainFontCdn", "https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;600&display=swap",
        type = SettingType.URL, fieldName = "Általános betűtípus CDN",
        description = "Az általános betűtípus CDN URL-je", serverSideOnly = true
    )

    val mainFontWeight = StringSettingRef(componentSettingService, component,
        "mainFontWeight", "300", fieldName = "Általános betűtípus vastagsága",
        description = "Az általános betűtípus vastagsága"
    )

    val displayFontName = StringSettingRef(componentSettingService, component,
        "displayFontName", "'Bebas Neue', cursive", fieldName = "Kiemelt betűtípus",
        description = "A kiemelt (pl. fejlécként megjelenő) betűtípus neve. Ha üres akkor az általános lesz használva."
    )

    val displayFontCdn = StringSettingRef(componentSettingService, component,
        "displayFontCdn", "https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap", type = SettingType.URL,
        fieldName = "Kiemelt betűtípus CDN", description = "A kiemelt betűtípus CDN URL-je", serverSideOnly = true
    )

    val displayFontWeight = StringSettingRef(componentSettingService, component,
        "displayFontWeight", "400", fieldName = "Kiemelt betűtípus vastagsága",
        description = "A kiemelt betűtípus vastagsága"
    )

}
