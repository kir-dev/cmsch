package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["team"],
    havingValue = "true",
    matchIfMissing = false
)
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
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    var myMinRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES_FROM_ATTENDEE,
        fieldName = "Csapatom menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamListGroup by SettingGroup(fieldName = "Csapat lista")

    final var title by StringSettingRef("Csapatok",
        fieldName = "Csapatok lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Csapatok", serverSideOnly = true,
        fieldName = "Csapatok menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Csapatok menü jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    var showTeamsAtAll by BooleanSettingRef(serverSideOnly = true, fieldName = "Csapat lista kijelzése",
        description = "Ha ki van kapcsolva a akkor a csapat lista nincs leküldve")

    var showNotRacingTeams by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Nem versenyző csapatok kijelzése", description = "Azoknak is a kijelzése akik nem versenyeznek")

    var showNotManualTeams by BooleanSettingRef(serverSideOnly = true,
        fieldName = "Admin által nevezett csapatok is látszanak",
        description = "Azoknak is a kijelzése akiket az admin panelről neveztek")

    var sortByName by BooleanSettingRef(serverSideOnly = true, fieldName = "Név alapján rendezés",
        description = "Ha igaz, akkor ABC sorrendben vannak kiírva, ha hamis akkor pont alapján")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamCreateGroup by SettingGroup(fieldName = "Csapat létrehozás")

    var createTitle by StringSettingRef("Csapat készítés",
        fieldName = "Csapat készítés lap címe", description = "Ez jelenik meg a böngésző címsorában")

    var createMenuDisplayName by StringSettingRef("Csapat készítés", serverSideOnly = true,
        fieldName = "Csapat készítés neve", description = "Ez lesz a neve a menünek")

    var createMinRole by MinRoleSettingRef(setOf(RoleType.BASIC),
        fieldName = "Csapat készítés menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal")

    var teamCreationTopMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapat készítés felső szöveg", description = "Ha üres, akkor nincs ilyen")

    var creationEnabled by BooleanSettingRef(fieldName = "Csoport készítés engedélyezve",
        description = "Ha igaz, lehet csapatot készíteni")

    var teamEditTitle by StringSettingRef("Csapat szerkesztése", fieldName = "Csoport adatainak szerkesztés lap címe",
        description = "A csoport szerkesztési lapjának tetején megjelenő címe")

    var teamEditTopMessage by StringSettingRef("Adj meg leírást és logót!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csoport szerkesztés felső szöveg", description = "Ha üres, akkor nincs ilyen")

    var teamEditEnabled by BooleanSettingRef(true, fieldName = "Csoport szerkesztés engedélyezése",
        description = "Ha igaz, lehet a csapat leírását szerkeszteni")

    var teamLogoUploadEnabled by BooleanSettingRef(fieldName = "Csoport logó feltöltés engedélyezése",
        description = "Ha igaz, lehet a vezetőknek logót feltölteni")

    var joinEnabled by BooleanSettingRef(fieldName = "Csatlakozás engedélyezve",
        description = "Ha igaz, lehet csapathoz csatlakozni")

    var leaveEnabled by BooleanSettingRef(fieldName = "Kilépés engedélyezve",
        description = "Ha igaz, lehet ki lehet lépni csapatból")

    var grantPrivilegedRole by BooleanSettingRef(true, serverSideOnly = true,
        fieldName = "PRIVILEGED jog a csapat készítőjének",
        description = "Ha be val kapcsolva, akkor a csapat készítője PRIVILEGED jogot kap")

    var grantAttendeeRole by BooleanSettingRef(true, serverSideOnly = true, fieldName = "ATTENDEE jog a csapattagoknak",
        description = "Ha be val kapcsolva, akkor a csapat tagjai ATTENDEE jogot kapnak (onnantól él, hogy be lett kapcsolva)")

    var nameRegex by StringSettingRef("^[A-Za-z0-9 _\\-ÁáÉéÍíÓóÖöŐőÚúÜüŰű]{1,32}$", serverSideOnly = true,
        fieldName = "Csapatnév regex", description = "Ez alapján megy majd a validálás")

    var nameBlocklist by StringSettingRef("test, dev", serverSideOnly = true, fieldName = "Tiltott nevek",
        description = "Tiltott csapatnevek vesszővel elválasztva")

    var racesByDefault by BooleanSettingRef(true, fieldName = "Alapból versenyzik",
        description = "Ha be van kapcsolva, akkor a csapat automatikusan versenyző státuszban van")

    var selectableByDefault by BooleanSettingRef(true, fieldName = "Alapból lehet bele jelentkezni",
        description = "Ha be van kapcsolva, akkor a csapatba automatikusan lehet jelentkezni")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamAdminGroup by SettingGroup(fieldName = "Csapat admin felület")

    var adminTitle by StringSettingRef("Csapatom kezelése",
        fieldName = "Admin lap címe", description = "Ez jelenik meg a böngésző címsorában")

    var adminMenuDisplayName by StringSettingRef("Csapatom kezelése", serverSideOnly = true,
        fieldName = "Admin menü neve", description = "Ez lesz a neve a menünek")

    var adminMinRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES_FROM_PRIVILEGED,
        fieldName = "Admin oldal jogosultságai", description = "Melyik roleokkal nyitható meg az admin oldal")

    var togglePermissionEnabled by BooleanSettingRef(true, fieldName = "Jogosultság adás állítása",
        description = "Ha be van kapcsolva, akkor lehet jogosultságokat állítani")

    var kickEnabled by BooleanSettingRef(true, fieldName = "Kidobás gomb",
        description = "Ha be val kapcsolva, akkor csapat vezetők kirakhatnak embereket")

    var promoteLeadershipEnabled by BooleanSettingRef(true, fieldName = "Jogosultság átadása",
        description = "Ha be val kapcsolva, akkor át lehet adni a vezetőséget másnak")

    var leaderNotes by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Üzenet a CSK-knak",
        description = "A csapat dashboardnál megjelenő üzenet a CSK-k nak. Ha üres nem látszik.")

    var showTasks by BooleanSettingRef(true, fieldName = "Feladatok mutatása",
        description = "Ha be val kapcsolva, akkor az elvégzendő feladatok látszódnak")

    var showAdvertisedForms by BooleanSettingRef(true, fieldName = "Formok mutatása",
        description = "Ha be val kapcsolva, akkor a hírdetett flaggel ellátott formokat megjelenítjük a csapatnál")

    /// -------------------------------------------------------------------------------------------------------------------

    val teamDetailsGroup by SettingGroup(fieldName = "Csapat adatlap")

    var showTeamDetails by BooleanSettingRef(fieldName = "Csoport részleteinek mutatása",
        description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek")

    var showTeamMembersPublicly by BooleanSettingRef(fieldName = "Csoport tagjai publikusak",
        description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek")

    var showTeamScore by BooleanSettingRef(fieldName = "Csoport pontjának kijelzése",
        description = "Ha be val kapcsolva, akkor az adatlapon kint lesz a csoport pontjainak a mutatója")

    var showTeamScoreDetailsButton by BooleanSettingRef(fieldName = "Csoport részletes pontjaihoz gomb",
        description = "Ha be val kapcsolva, akkor megjelenik egy gomb a csapat részletes pont listájához")

    var showRaceButton by BooleanSettingRef(false, fieldName = "Mérés gomb látszódik",
        description = "Ha igaz, látszik a gomb")


    /// -------------------------------------------------------------------------------------------------------------------

    val statGroup by SettingGroup(fieldName = "Csapat statisztika")

    var membersStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Tagok számának kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hányan vannak")

    var membersStatHeader by StringSettingRef("Tagok", serverSideOnly = true,
        fieldName = "Tagok fejléce", description = "Ez lesz a neve a statisztikának")

    var placeStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Helyezés kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hanyadik a csapat (Leaderboard komponens kell hozzá)")

    var placeStatHeader by StringSettingRef("Helyezés", serverSideOnly = true,
        fieldName = "Helyezés fejléce", description = "Ez lesz a neve a statisztikának")

    var scoreStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Pontszám kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hány pontja van a csapatnak (Leaderboard komponens kell hozzá)")

    var scoreStatHeader by StringSettingRef("Pontszám", serverSideOnly = true,
        fieldName = "Pontszám fejléce", description = "Ez lesz a neve a statisztikának")

    var qrFightStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "QR Fight kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat által megszerzett pontok és tornyok látszódnak (QRFight komponens kell hozzá)")

    var qrTokenStatHeader by StringSettingRef("Megtalált QR kód", serverSideOnly = true,
        fieldName = "QR kódok fejléce", description = "Ez lesz a neve a statisztikának")

    var qrTowerStatHeader by StringSettingRef("Megszerzett tornyok", serverSideOnly = true,
        fieldName = "Tornyok fejléce", description = "Ez lesz a neve a statisztikának")

    var raceStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Verseny eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy ki és milyen eredménnyel a legjobb (Verseny komponens kell hozzá)")

    var raceStatHeader by StringSettingRef("Sörmérés", serverSideOnly = true,
        fieldName = "Verseny fejléce", description = "Ez lesz a neve a statisztikának")

    var riddleStatEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Riddle eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hogy állnak a ridleökkel (Riddle komponens kell hozzá)")

    var riddleStatHeader by StringSettingRef("Riddleök", serverSideOnly = true,
        fieldName = "Riddle fejléce", description = "Ez lesz a neve a statisztikának")

}
