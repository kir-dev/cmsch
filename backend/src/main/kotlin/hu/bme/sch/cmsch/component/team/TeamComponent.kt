package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.team"])
class TeamComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "team",
    "/teams",
    "Csapatok",
    ControlPermissions.PERMISSION_CONTROL_TEAM,
    listOf(TeamJoinRequestEntity::class),
    env
) {

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        val result = mutableListOf<MenuSettingItem>()
        if (myMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@my",
                myMenuDisplayName, "/my-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        if (createMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@create",
                createMenuDisplayName, "/create-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        if (adminMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@admin",
                adminMenuDisplayName, "/edit-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

    /// -------------------------------------------------------------------------------------------------------------------

    val myTeamGroup by SettingGroup(fieldName = "Csapatom")

    var myTitle by StringSettingRef("Csapatom",
        fieldName = "Csapatom lap címe", description = "Ez jelenik meg a böngésző címsorában")

    var myMenuDisplayName by StringSettingRef("Csapatom", serverSideOnly = true,
        fieldName = "Menü neve", description = "A menüpont neve")

    var myMinRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES_FROM_ATTENDEE,
        fieldName = "Csapatom menü jogosultságai", description = "Mely szerepkörökkel nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamListGroup by SettingGroup(fieldName = "Csapatlista")

    final var title by StringSettingRef("Csapatok",
        fieldName = "Csapatok lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Csapatok", serverSideOnly = true,
        fieldName = "Csapatok menü neve", description = "A menüpont neve")

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Csapatok menü jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    var showTeamsAtAll by BooleanSettingRef(serverSideOnly = true, fieldName = "Csapatlista megjelenítése",
        description = "Kikapcsolt állapotban a csapatlista nem kerül leküldésre a kliensnek")

    var searchEnabled by BooleanSettingRef(true, fieldName = "Keresés engedélyezése",
        description = "Bekapcsolt állapotban lehet keresni a csapatok között")

    var showNotRacingTeams by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Nem versenyző csapatok megjelenítése", description = "A nem versenyző státuszú csapatok megjelenítése")

    var showNotManualTeams by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Admin által nevezett csapatok megjelenítése",
        description = "Az admin felületről létrehozott/nevezett csapatok megjelenítése")

    var sortByName by BooleanSettingRef(serverSideOnly = true, fieldName = "Rendezés név alapján",
        description = "Bekapcsolt állapotban ABC sorrendben, kikapcsolva pontszám alapján történik a rendezés")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamCreateGroup by SettingGroup(fieldName = "Csapat létrehozása")

    var createTitle by StringSettingRef("Csapat készítés",
        fieldName = "Csapatkészítés lap címe", description = "Ez jelenik meg a böngésző címsorában")

    var createMenuDisplayName by StringSettingRef("Csapat készítés", serverSideOnly = true,
        fieldName = "Csapatkészítés menü neve", description = "A menüpont neve")

    var createMinRole by MinRoleSettingRef(setOf(RoleType.BASIC),
        fieldName = "Csapatkészítés menü jogosultságai", description = "Mely szerepkörökkel nyitható meg az oldal")

    var teamCreationTopMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapatkészítés felső szöveg", description = "Ha üres, nem jelenik meg")

    var creationEnabled by BooleanSettingRef(fieldName = "Csapatkészítés engedélyezve",
        description = "Bekapcsolt állapotban a felhasználók hozhatnak létre csapatot")

    var teamEditTitle by StringSettingRef("Csapat szerkesztése", fieldName = "Csapatszerkesztés lap címe",
        description = "A csapatszerkesztő oldal fejlécének szövege")

    var teamEditTopMessage by StringSettingRef("Adj meg leírást és logót!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapatszerkesztés felső szöveg", description = "Ha üres, nem jelenik meg")

    var teamEditEnabled by BooleanSettingRef(true, fieldName = "Csapatszerkesztés engedélyezése",
        description = "Bekapcsolt állapotban a csapat leírása szerkeszthető")

    var teamLogoUploadEnabled by BooleanSettingRef(fieldName = "Csapatlogó feltöltés engedélyezése",
        description = "Bekapcsolt állapotban a vezetők tölthetnek fel logót")

    var joinEnabled by BooleanSettingRef(fieldName = "Csatlakozás engedélyezve",
        description = "Bekapcsolt állapotban engedélyezett a csatlakozás a csapatokhoz")

    var leaveEnabled by BooleanSettingRef(fieldName = "Kilépés engedélyezve",
        description = "Bekapcsolt állapotban engedélyezett a csapatból való kilépés")

    var grantPrivilegedRole by BooleanSettingRef(true, serverSideOnly = true,
        fieldName = "PRIVILEGED jog a csapatkészítőnek",
        description = "Bekapcsolt állapotban a csapat létrehozója PRIVILEGED szerepkört kap")

    var grantAttendeeRole by BooleanSettingRef(true, serverSideOnly = true, fieldName = "ATTENDEE jog a csapattagoknak",
        description = "Bekapcsolt állapotban a csapat tagjai ATTENDEE szerepkört kapnak")

    var nameRegex by StringSettingRef("^[A-Za-z0-9 _\\-ÁáÉéÍíÓóÖöŐőÚúÜüŰű]{1,32}$", serverSideOnly = true,
        fieldName = "Csapatnév szabály (Regex)", description = "A csapatnév ellenőrzésére szolgáló reguláris kifejezés")

    var nameBlocklist by StringSettingRef("test, dev", serverSideOnly = true, fieldName = "Tiltott nevek",
        description = "Tiltott csapatnevek vesszővel elválasztva")

    var racesByDefault by BooleanSettingRef(true, fieldName = "Alapból versenyzik",
        description = "Bekapcsolt állapotban az új csapatok automatikusan versenyző státuszt kapnak")

    var selectableByDefault by BooleanSettingRef(true, fieldName = "Alapból lehet jelentkezni",
        description = "Bekapcsolt állapotban az új csapatokba automatikusan lehet jelentkezni")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamAdminGroup by SettingGroup(fieldName = "Csapat admin felület")

    var adminTitle by StringSettingRef("Csapatom kezelése",
        fieldName = "Admin lap címe", description = "Ez jelenik meg a böngésző címsorában")

    var adminMenuDisplayName by StringSettingRef("Csapatom kezelése", serverSideOnly = true,
        fieldName = "Admin menü neve", description = "A menüpont neve")

    var adminMinRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES_FROM_PRIVILEGED,
        fieldName = "Admin oldal jogosultságai", description = "Mely szerepkörökkel nyitható meg az admin oldal")

    var togglePermissionEnabled by BooleanSettingRef(true, fieldName = "Jogosultságok kezelése",
        description = "Bekapcsolt állapotban a vezetők állíthatják a tagok jogosultságait")

    var kickEnabled by BooleanSettingRef(true, fieldName = "Tagok eltávolítása",
        description = "Bekapcsolt állapotban a csapatvezetők eltávolíthatnak tagokat")

    var promoteLeadershipEnabled by BooleanSettingRef(true, fieldName = "Vezetőség átadása",
        description = "Bekapcsolt állapotban a vezetés átadható más tagnak")

    var leaderNotes by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Üzenet a vezetőknek",
        description = "A csapat dashboardon megjelenő üzenet a vezetők számára. Ha üres, nem jelenik meg.")

    var showTasks by BooleanSettingRef(true, fieldName = "Feladatok megjelenítése",
        description = "Bekapcsolt állapotban az elvégzendő feladatok láthatóak")

    var showAdvertisedForms by BooleanSettingRef(true, fieldName = "Űrlapok megjelenítése",
        description = "Bekapcsolt állapotban a hirdetett űrlapok megjelennek a csapatnál")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamDetailsGroup by SettingGroup(fieldName = "Csapat adatlap")

    var showTeamDetails by BooleanSettingRef(fieldName = "Részletek megjelenítése",
        description = "Bekapcsolt állapotban a csapatok adatai megtekinthetőek")

    var showTeamMembersPublicly by BooleanSettingRef(fieldName = "Tagok publikussá tétele",
        description = "Bekapcsolt állapotban a csapatok taglistája bárki számára látható")

    var showTeamScore by BooleanSettingRef(fieldName = "Pontszám megjelenítése",
        description = "Bekapcsolt állapotban az adatlapon látható a csapat összpontszáma")

    var showTeamScoreDetailsButton by BooleanSettingRef(fieldName = "Részletes pontszám gomb",
        description = "Bekapcsolt állapotban megjelenik egy gomb a részletes pontszámokhoz")

    var showRaceButton by BooleanSettingRef(false, fieldName = "Mérés gomb megjelenítése",
        description = "Bekapcsolt állapotban látható a mérés gomb")


    /// -------------------------------------------------------------------------------------------------------------------

    val statGroup by SettingGroup(fieldName = "Csapat statisztika")

    var membersStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Tagok számának megjelenítése",
        description = "Bekapcsolt állapotban látható a csapat létszáma")

    var membersStatHeader by StringSettingRef("Tagok", serverSideOnly = true,
        fieldName = "Tagok fejléce", description = "A statisztika megnevezése")

    var placeStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Helyezés megjelenítése",
        description = "Bekapcsolt állapotban látható a csapat helyezése (Leaderboard komponens szükséges)")

    var placeStatHeader by StringSettingRef("Helyezés", serverSideOnly = true,
        fieldName = "Helyezés fejléce", description = "A statisztika megnevezése")

    var scoreStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Pontszám megjelenítése",
        description = "Bekapcsolt állapotban látható a csapat pontszáma (Leaderboard komponens szükséges)")

    var scoreStatHeader by StringSettingRef("Pontszám", serverSideOnly = true,
        fieldName = "Pontszám fejléce", description = "A statisztika megnevezése")

    var qrFightStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "QR Fight megjelenítése",
        description = "Bekapcsolt állapotban láthatóak a megszerzett tornyok és pontok (QRFight komponens szükséges)")

    var qrTokenStatHeader by StringSettingRef("Megtalált QR kód", serverSideOnly = true,
        fieldName = "QR kódok fejléce", description = "A statisztika megnevezése")

    var qrTowerStatHeader by StringSettingRef("Megszerzett tornyok", serverSideOnly = true,
        fieldName = "Tornyok fejléce", description = "A statisztika megnevezése")

    var raceStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Versenyeredmény megjelenítése",
        description = "Bekapcsolt állapotban látható a legjobb versenyeredmény (Verseny komponens szükséges)")

    var raceStatHeader by StringSettingRef("Sörmérés", serverSideOnly = true,
        fieldName = "Verseny fejléce", description = "A statisztika megnevezése")

    var riddleStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Riddle eredmény megjelenítése",
        description = "Bekapcsolt állapotban látható a riddle haladás (Riddle komponens szükséges)")

    var riddleStatHeader by StringSettingRef("Riddleök", serverSideOnly = true,
        fieldName = "Riddle fejléce", description = "A statisztika megnevezése")

}
