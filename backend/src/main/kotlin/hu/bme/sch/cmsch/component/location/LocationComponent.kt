package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["location"],
    havingValue = "true",
    matchIfMissing = false
)
class LocationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "location",
    "/map",
    "Helymeghatározás",
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            locationGroup,
            menuDisplayName,
            minRole,

            appearanceGroup,
            topMessage,
            bottomMessage,

            colorGroup,
            defaultGroupColor,
            blackGroupName,
            blueGroupName,
            cyanGroupName,
            pinkGroupName,
            orangeGroupName,
            greenGroupName,
            redGroupName,
            whiteGroupName,
            yellowGroupName,
            purpleGroupName,
            grayGroupName,

            appGroup,
            installGuide,
            androidAppUrl,
            iosAppUrl,

            displayGroup,
            showUserName,
            showAlias,
            showGroupName,
            visibleDuration,
        )
    }

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Térkép", serverSideOnly = true,
        fieldName = "Térkép menü neve", description = "Ez lesz a neve a menünek"
    )

    val locationGroup = SettingGroup(component, "locationGroup", fieldName = "Helymegosztás")

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = SettingGroup(component, "appearanceGroup", fieldName = "Megjelenés")

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val bottomMessage = StringSettingRef(componentSettingService, component,
        "bottomMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal alján megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val colorGroup = SettingGroup(component, "colorGroup", fieldName = "Csoport színek")

    val defaultGroupColor = StringSettingRef(componentSettingService, component,
        "defaultGroupColor", "#FF0000", type = SettingType.COLOR,
        fieldName = "Alapértelmezett csoport színe", serverSideOnly = true
    )

    val blackGroupName = StringSettingRef(componentSettingService, component,
        "blackGroupName", "LEAD", fieldName = "Fekete csoport színe", serverSideOnly = true
    )

    val blueGroupName = StringSettingRef(componentSettingService, component,
        "blueGroupName", "SENIOR", fieldName = "Kék csoport neve", serverSideOnly = true
    )

    val cyanGroupName = StringSettingRef(componentSettingService, component,
        "cyanGroupName", "CONTROL", fieldName = "Türkiz csoport neve", serverSideOnly = true
    )

    val pinkGroupName = StringSettingRef(componentSettingService, component,
        "pinkGroupName", "SUPPORT", fieldName = "Rózsaszín csoport neve", serverSideOnly = true
    )

    val orangeGroupName = StringSettingRef(componentSettingService, component,
        "orangeGroupName", "KIRDEV", fieldName = "Narancs csoport neve", serverSideOnly = true
    )

    val greenGroupName = StringSettingRef(componentSettingService, component,
        "greenGroupName", "", fieldName = "Zöld csoport neve", serverSideOnly = true
    )

    val redGroupName = StringSettingRef(componentSettingService, component,
        "redGroupName", "", fieldName = "Piros csoport neve", serverSideOnly = true
    )

    val whiteGroupName = StringSettingRef(componentSettingService, component,
        "whiteGroupName", "", fieldName = "Fehér csoport neve", serverSideOnly = true
    )

    val yellowGroupName = StringSettingRef(componentSettingService, component,
        "yellowGroupName", "", fieldName = "Sárga csoport neve", serverSideOnly = true
    )

    val purpleGroupName = StringSettingRef(componentSettingService, component,
        "purpleGroupName", "", fieldName = "Lila csoport neve", serverSideOnly = true
    )

    val grayGroupName = StringSettingRef(componentSettingService, component,
        "grayGroupName", "", fieldName = "Szürke csoport neve", serverSideOnly = true
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appGroup = SettingGroup(component, "appGroup", fieldName = "Tracker alkalmazás")

    val installGuide = StringSettingRef(componentSettingService, component,
        "installGuide",
        "Annak érdekében, hogy a tanköröd elveszett tagjai és a gárdatankörisek is mindig megtaláljanak\n" +
                " egyszerűen, létre hoztunk egy helymegosztási lehetőséget. A használatához le kell töltened egy\n" +
                " Android vagy iOS appot.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Telepítési útmutató", description = "A Helymeghatározás menüben látszik"
    )

    val androidAppUrl = StringSettingRef(componentSettingService, component,
        "androidAppUrl", "https://kir-dev.hu/ly/androidbacon", type = SettingType.URL,
        serverSideOnly = true, fieldName = "Android App URL-je"
    )

    val iosAppUrl = StringSettingRef(componentSettingService, component,
        "iosAppUrl", "https://kir-dev.hu/ly/iosbacon", type = SettingType.URL,
        serverSideOnly = true, fieldName = "iOS App URL-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingGroup(component, "displayGroup", fieldName = "Megjelenítés")

    val showUserName = BooleanSettingRef(componentSettingService, component,
        "showUserName", false, fieldName = "Felhasználó nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó neve is szerepel a marker alatt"
    )

    val showAlias = BooleanSettingRef(componentSettingService, component,
        "showAlias", false, fieldName = "Becenév nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó beceneve is szerepel a marker alatt"
    )

    val showGroupName = BooleanSettingRef(componentSettingService, component,
        "showGroupName", true, fieldName = "Csoport nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a csoport neve is szerepel a marker alatt"
    )

    val visibleDuration = NumberSettingRef(componentSettingService, component,
        "visibleDuration", 600, fieldName = "Láthatóság ideje", serverSideOnly = true, strictConversion = false,
        description = "Ennyi ideig látszódik frissítés nélkül egy marker (másodpercben)"
    )

}
