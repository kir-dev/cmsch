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
    componentSettingService,
    "location",
    "/map",
    "Helymeghatározás",
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    listOf(),
    env
) {

    val locationGroup by SettingGroup(fieldName = "Helymegosztás")

    final override var menuDisplayName by StringSettingRef("Térkép", serverSideOnly = true,
        fieldName = "Térkép menü neve", description = "Ez lesz a neve a menünek")


    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup by SettingGroup(fieldName = "Megjelenés")

    var topMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

    var bottomMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal alján megjelenő szöveg", description = "Ha üres akkor nincs ilyen")

    /// -------------------------------------------------------------------------------------------------------------------

    val colorGroup by SettingGroup(fieldName = "Csoport színek")

    var defaultGroupColor by StringSettingRef("#FF0000", type = SettingType.COLOR,
        fieldName = "Alapértelmezett csoport színe", serverSideOnly = true)

    var blackGroupName by StringSettingRef("LEAD", fieldName = "Fekete csoport színe", serverSideOnly = true)

    var blueGroupName by StringSettingRef("SENIOR", fieldName = "Kék csoport neve", serverSideOnly = true)

    var cyanGroupName by StringSettingRef("CONTROL", fieldName = "Türkiz csoport neve", serverSideOnly = true)

    var pinkGroupName by StringSettingRef("SUPPORT", fieldName = "Rózsaszín csoport neve", serverSideOnly = true)

    var orangeGroupName by StringSettingRef("KIRDEV", fieldName = "Narancs csoport neve", serverSideOnly = true)

    var greenGroupName by StringSettingRef(fieldName = "Zöld csoport neve", serverSideOnly = true)

    var redGroupName by StringSettingRef(fieldName = "Piros csoport neve", serverSideOnly = true)

    var whiteGroupName by StringSettingRef(fieldName = "Fehér csoport neve", serverSideOnly = true)

    var yellowGroupName by StringSettingRef(fieldName = "Sárga csoport neve", serverSideOnly = true)

    var purpleGroupName by StringSettingRef(fieldName = "Lila csoport neve", serverSideOnly = true)

    var grayGroupName by StringSettingRef(fieldName = "Szürke csoport neve", serverSideOnly = true)

    /// -------------------------------------------------------------------------------------------------------------------

    val appGroup by SettingGroup(fieldName = "Tracker alkalmazás")

    var installGuide by StringSettingRef("Annak érdekében, hogy a tanköröd elveszett tagjai és a gárdatankörisek is mindig megtaláljanak\n" +
            " egyszerűen, létre hoztunk egy helymegosztási lehetőséget. A használatához le kell töltened egy\n" +
            " Android vagy iOS appot.", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Telepítési útmutató", description = "A Helymeghatározás menüben látszik")

    var androidAppUrl by StringSettingRef("https://kir-dev.hu/ly/androidbacon", type = SettingType.URL,
        serverSideOnly = true, fieldName = "Android App URL-je")

    var iosAppUrl by StringSettingRef("https://kir-dev.hu/ly/iosbacon", type = SettingType.URL,
        serverSideOnly = true, fieldName = "iOS App URL-je")

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Megjelenítés")

    var showUserName by BooleanSettingRef(false, fieldName = "Felhasználó nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó neve is szerepel a marker alatt")

    var showAlias by BooleanSettingRef(false, fieldName = "Becenév nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a felhasználó beceneve is szerepel a marker alatt")

    var showGroupName by BooleanSettingRef(true, fieldName = "Csoport nevének kiírása", serverSideOnly = true,
        description = "Ha be van kapcsolva, akkor a csoport neve is szerepel a marker alatt")

    var visibleDuration by NumberSettingRef(600, fieldName = "Láthatóság ideje",
        serverSideOnly = true, strictConversion = false,
        description = "Ennyi ideig látszódik frissítés nélkül egy marker (másodpercben)")

}
