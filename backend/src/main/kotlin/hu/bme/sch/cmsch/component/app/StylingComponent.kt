package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class StylingComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("style", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            styleGroup,
            backgroundColor,
            containerColor,
            textColor,
            brandingColor,
            backgroundUrl,
            mobileBackgroundUrl,

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

    val styleGroup = SettingProxy(componentSettingService, component,
        "styleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Oldal stílus beállításai",
        description = "Az oldal stílusának színei, háttérképek"
    )

    val backgroundColor = SettingProxy(componentSettingService, component,
        "backgroundColor", "#FFFFFF", type = SettingType.COLOR,
        fieldName = "Háttérszín", description = "Az oldal hátterének a színe, ha nincs kép megadva, akkor ez látszik"
    )

    val containerColor = SettingProxy(componentSettingService, component,
        "containerColor", "transparent", type = SettingType.COLOR,
        fieldName = "Lap színe", description = "A lap tartamának háttérszíne"
    )

    val textColor = SettingProxy(componentSettingService, component,
        "textColor", "#000000", type = SettingType.COLOR,
        fieldName = "Szövegszín", description = "A megjelenő szövegek színe"
    )

    val brandingColor = SettingProxy(componentSettingService, component,
        "brandingColor", "#880000", type = SettingType.COLOR,
        fieldName = "Brand szín", description = "Az oldal színes elemei ez alapján kerülnek kiszínezésre"
    )

    val backgroundUrl = SettingProxy(componentSettingService, component,
        "backgroundUrl", "", type = SettingType.URL,
        fieldName = "Háttérkép", description = "Nagy felbontáson megjelenő háttérkép URL-je. Ha üres, akkor nincs háttér beállítva."
    )

    val mobileBackgroundUrl = SettingProxy(componentSettingService, component,
        "mobileBackgroundUrl", "", type = SettingType.URL,
        fieldName = "Mobil háttérkép", description = "Mobilon megjelenő háttér URL-je. Ha üres, akkor nincs háttér beállítva."
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
        fieldName = "Általános betűtípus CDN", description = "Az általános betűtípus CDN URL-je"
    )

    val mainFontWeight = SettingProxy(componentSettingService, component,
        "mainFontWeight", "300", type = SettingType.NUMBER,
        fieldName = "Általános betűtípus vastagsága", description = "Az általános betűtípus vastagsága"
    )

    val displayFontName = SettingProxy(componentSettingService, component,
        "displayFontName", "'Bebas Neue', cursive", type = SettingType.TEXT,
        fieldName = "Kiemelt betűtípus", description = "A kiemelt (pl. fejlécként megjelenő) betűtípus neve. Ha üres akkor az álltalános lesz használva."
    )

    val displayFontCdn = SettingProxy(componentSettingService, component,
        "displayFontCdn", "https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap", type = SettingType.TEXT,
        fieldName = "Kiemelt betűtípus CDN", description = "A kiemelt betűtípus CDN URL-je"
    )

    val displayFontWeight = SettingProxy(componentSettingService, component,
        "displayFontWeight", "400", type = SettingType.NUMBER,
        fieldName = "Kiemelt betűtípus vastagsága", description = "A kiemelt betűtípus vastagsága"
    )

}
