package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            locationGroup,
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

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Térkép", serverSideOnly = true,
        fieldName = "Térkép menü neve", description = "Ez lesz a neve a menünek"
    )

    val locationGroup = SettingProxy(componentSettingService, component,
        "locationGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Helymegosztás",
        description = ""
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = SettingProxy(componentSettingService, component,
        "appearanceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Megjelenés",
        description = ""
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val bottomMessage = SettingProxy(componentSettingService, component,
        "bottomMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal alján megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val colorGroup = SettingProxy(componentSettingService, component,
        "colorGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csoport színek", serverSideOnly = true,
        description = ""
    )

    val defaultGroupColor = SettingProxy(componentSettingService, component,
        "defaultGroupColor", "#FF0000", type = SettingType.COLOR,
        fieldName = "Alapértelmezett csoport színe", serverSideOnly = true
    )

    val blackGroupName = SettingProxy(componentSettingService, component,
        "blackGroupName", "LEAD", type = SettingType.TEXT,
        fieldName = "Fekete csoport színe", serverSideOnly = true
    )

    val blueGroupName = SettingProxy(componentSettingService, component,
        "blueGroupName", "SENIOR", type = SettingType.TEXT,
        fieldName = "Kék csoport neve", serverSideOnly = true
    )

    val cyanGroupName = SettingProxy(componentSettingService, component,
        "cyanGroupName", "CONTROL", type = SettingType.TEXT,
        fieldName = "Türkiz csoport neve", serverSideOnly = true
    )

    val pinkGroupName = SettingProxy(componentSettingService, component,
        "pinkGroupName", "SUPPORT", type = SettingType.TEXT,
        fieldName = "Rózsaszín csoport neve", serverSideOnly = true
    )

    val orangeGroupName = SettingProxy(componentSettingService, component,
        "orangeGroupName", "KIRDEV", type = SettingType.TEXT,
        fieldName = "Narancs csoport neve", serverSideOnly = true
    )

    val greenGroupName = SettingProxy(componentSettingService, component,
        "greenGroupName", "", type = SettingType.TEXT,
        fieldName = "Zöld csoport neve", serverSideOnly = true
    )

    val redGroupName = SettingProxy(componentSettingService, component,
        "redGroupName", "", type = SettingType.TEXT,
        fieldName = "Piros csoport neve", serverSideOnly = true
    )

    val whiteGroupName = SettingProxy(componentSettingService, component,
        "whiteGroupName", "", type = SettingType.TEXT,
        fieldName = "Fehér csoport neve", serverSideOnly = true
    )

    val yellowGroupName = SettingProxy(componentSettingService, component,
        "yellowGroupName", "", type = SettingType.TEXT,
        fieldName = "Sárga csoport neve", serverSideOnly = true
    )

    val purpleGroupName = SettingProxy(componentSettingService, component,
        "purpleGroupName", "", type = SettingType.TEXT,
        fieldName = "Lila csoport neve", serverSideOnly = true
    )

    val grayGroupName = SettingProxy(componentSettingService, component,
        "grayGroupName", "", type = SettingType.TEXT,
        fieldName = "Szürke csoport neve", serverSideOnly = true
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appGroup = SettingProxy(componentSettingService, component,
        "appGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tracker alkalmazás",
        description = "", serverSideOnly = true
    )

    val installGuide = SettingProxy(componentSettingService, component,
        "installGuide",
        "Annak érdekében, hogy a tanköröd elveszett tagjai és a gárdatankörisek is mindig megtaláljanak\n" +
                " egyszerűen, létre hoztunk egy helymegosztási lehetőséget. A használatához le kell töltened egy\n" +
                " Android vagy iOS appot.",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Telepítési útmutató", description = "A Helymeghatározás menüben látszik"
    )

    val androidAppUrl = SettingProxy(componentSettingService, component,
        "androidAppUrl", "https://kir-dev.hu/ly/androidbacon", type = SettingType.TEXT,
        serverSideOnly = true, fieldName = "Android App URL-je"
    )

    val iosAppUrl = SettingProxy(componentSettingService, component,
        "iosAppUrl", "https://kir-dev.hu/ly/iosbacon", type = SettingType.TEXT,
        serverSideOnly = true, fieldName = "iOS App URL-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingProxy(componentSettingService, component,
        "displayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Megjelenítés",
        description = "", serverSideOnly = true
    )

    val showUserName = SettingProxy(componentSettingService, component,
        "showUserName", "false", type = SettingType.BOOLEAN,
        fieldName = "Felhasználó nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó neve is szerepel a marker alatt"
    )

    val showAlias = SettingProxy(componentSettingService, component,
        "showAlias", "false", type = SettingType.BOOLEAN,
        fieldName = "Becenév nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó beceneve is szerepel a marker alatt"
    )

    val showGroupName = SettingProxy(componentSettingService, component,
        "showGroupName", "true", type = SettingType.BOOLEAN,
        fieldName = "Csoport nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a csoport neve is szerepel a marker alatt"
    )

    val visibleDuration = SettingProxy(componentSettingService, component,
        "visibleDuration", "600", type = SettingType.NUMBER,
        fieldName = "Láthatóság ideje", serverSideOnly = true,
        description = "Ennyi ideig látszódik frissítés nélkül egy marker (másodpercben)"
    )

}
