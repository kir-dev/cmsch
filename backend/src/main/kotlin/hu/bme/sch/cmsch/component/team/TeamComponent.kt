package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    "team",
    "/teams",
    "Csapatok",
    ControlPermissions.PERMISSION_CONTROL_TEAM,
    listOf(TeamJoinRequestEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            myTeamGroup,
            myTitle, myMenuDisplayName, myMinRole,

            teamListGroup,
            title, menuDisplayName, minRole,
            showTeamsAtAll,
            showNotRacingTeams,
            showNotManualTeams,
            sortByName,

            teamCreateGroup,
            createTitle, createMenuDisplayName, createMinRole,
            teamCreationTopMessage,
            creationEnabled,
            teamEditTitle,
            teamEditTopMessage,
            teamEditEnabled,
            teamLogoUploadEnabled,
            joinEnabled,
            leaveEnabled,
            grantPrivilegedRole,
            grantAttendeeRole,
            nameRegex,
            nameBlocklist,
            racesByDefault,
            selectableByDefault,

            teamAdminGroup,
            adminTitle,
            adminMenuDisplayName,
            adminMinRole,
            togglePermissionEnabled,
            kickEnabled,
            promoteLeadershipEnabled,
            leaderNotes,
            showTasks,
            showAdvertisedForms,

            teamDetailsGroup,
            showTeamDetails,
            showTeamMembersPublicly,
            showTeamScore,
            showTeamScoreDetailsButton,
            showRaceButton,

            statGroup,
            membersStatEnabled,
            membersStatHeader,
            placeStatEnabled,
            placeStatHeader,
            scoreStatEnabled,
            scoreStatHeader,
            qrFightStatEnabled,
            qrTokenStatHeader,
            qrTowerStatHeader,
            raceStatEnabled,
            raceStatHeader,
            riddleStatEnabled,
            riddleStatHeader,
        )
    }

    override fun getAdditionalMenus(role: RoleType): List<MenuSettingItem> {
        val result = mutableListOf<MenuSettingItem>()
        if (myMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@my",
                myMenuDisplayName.getValue(), "/my-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        if (createMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@create",
                createMenuDisplayName.getValue(), "/create-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        if (adminMinRole.isAvailableForRole(role) || role.isAdmin) {
            result.add(MenuSettingItem(
                this.javaClass.simpleName + "@admin",
                adminMenuDisplayName.getValue(), "/edit-team", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

    /// -------------------------------------------------------------------------------------------------------------------

    val myTeamGroup = ControlGroup(component, "myTeamGroup", fieldName = "Csapatom")

    val myTitle = StringSettingRef(componentSettingService, component,
        "myTitle", "Csapatom", fieldName = "Csapatom lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val myMenuDisplayName = StringSettingRef(componentSettingService, component,
        "myMenuDisplayName", "Csapatom", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    val myMinRole = MinRoleSettingRef(componentSettingService, component,
        "myMinRole", MinRoleSettingRef.ALL_ROLES_FROM_ATTENDEE,
        fieldName = "Csapatom menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamListGroup = ControlGroup(component, "teamListGroup", fieldName = "Csapat lista")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Csapatok", fieldName = "Csapatok lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Csapatok", serverSideOnly = true,
        fieldName = "Csapatok menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Csapatok menü jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val showTeamsAtAll = BooleanSettingRef(componentSettingService, component,
        "showTeamsAtAll", false, serverSideOnly = true, fieldName = "Csapat lista kijelzése",
        description = "Ha ki van kapcsolva a akkor a csapat lista nincs leküldve"
    )

    val showNotRacingTeams = BooleanSettingRef(componentSettingService, component,
        "showNotRacingTeams", false, serverSideOnly = true, fieldName = "Nem versenyző csapatok kijelzése",
        description = "Azoknak is a kijelzése akik nem versenyeznek"
    )

    val showNotManualTeams = BooleanSettingRef(componentSettingService, component,
        "showNotManualTeams", false, serverSideOnly = true, fieldName = "Admin által nevezett csapatok is látszanak",
        description = "Azoknak is a kijelzése akiket az admin panelről neveztek"
    )

    val sortByName = BooleanSettingRef(componentSettingService, component,
        "sortByName", false, serverSideOnly = true, fieldName = "Név alapján rendezés",
        description = "Ha igaz, akkor ABC sorrendben vannak kiírva, ha hamis akkor pont alapján"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamCreateGroup = ControlGroup(component, "teamCreateGroup", fieldName = "Csapat létrehozás")

    val createTitle = StringSettingRef(componentSettingService, component,
        "createTitle", "Csapat készítés",
        fieldName = "Csapat készítés lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val createMenuDisplayName = StringSettingRef(componentSettingService, component,
        "createMenuDisplayName", "Csapat készítés", serverSideOnly = true,
        fieldName = "Csapat készítés neve", description = "Ez lesz a neve a menünek"
    )

    val createMinRole = MinRoleSettingRef(componentSettingService, component,
        "createMinRole", RoleType.BASIC.name,
        fieldName = "Csapat készítés menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val teamCreationTopMessage = StringSettingRef(componentSettingService, component,
        "teamCreationTopMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapat készítés felső szöveg", description = "Ha üres, akkor nincs ilyen"
    )

    val creationEnabled = BooleanSettingRef(componentSettingService, component,
        "creationEnabled", false, fieldName = "Csoport készítés engedélyezve",
        description = "Ha igaz, lehet csapatot készíteni"
    )

    val teamEditTitle = StringSettingRef(componentSettingService,
        component, "teamEditTitle", "Csapat szerkesztése", fieldName = "Csoport adatainak szerkesztés lap címe",
        description = "A csoport szerkesztési lapjának tetején megjelenő címe"
    )

    val teamEditTopMessage = StringSettingRef(componentSettingService, component,
        "teamEditTopMessage", "Adj meg leírást és logót!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csoport szerkesztés felső szöveg", description = "Ha üres, akkor nincs ilyen"
    )

    val teamEditEnabled = BooleanSettingRef(componentSettingService, component,
        "teamEditEnabled", true, fieldName = "Csoport szerkesztés engedélyezése",
        description = "Ha igaz, lehet a csapat leírását szerkeszteni"
    )

    val teamLogoUploadEnabled = BooleanSettingRef(componentSettingService, component,
        "teamLogoUploadEnabled", false, fieldName = "Csoport logó feltöltés engedélyezése",
        description = "Ha igaz, lehet a vezetőknek logót feltölteni"
    )

    val joinEnabled = BooleanSettingRef(componentSettingService, component,
        "joinEnabled", false, fieldName = "Csatlakozás engedélyezve",
        description = "Ha igaz, lehet csapathoz csatlakozni"
    )

    val leaveEnabled = BooleanSettingRef(componentSettingService, component,
        "leaveEnabled", false, fieldName = "Kilépés engedélyezve",
        description = "Ha igaz, lehet ki lehet lépni csapatból"
    )

    val grantPrivilegedRole = BooleanSettingRef(componentSettingService, component,
        "grantPrivilegedRole", true, serverSideOnly = true, fieldName = "PRIVILEGED jog a csapat készítőjének",
        description = "Ha be val kapcsolva, akkor a csapat készítője PRIVILEGED jogot kap"
    )

    val grantAttendeeRole = BooleanSettingRef(componentSettingService, component,
        "grantAttendeeRole", true, serverSideOnly = true, fieldName = "ATTENDEE jog a csapattagoknak",
        description = "Ha be val kapcsolva, akkor a csapat tagjai ATTENDEE jogot kapnak (onnantól él, hogy be lett kapcsolva)"
    )

    val nameRegex = StringSettingRef(componentSettingService, component,
        "nameRegex", "^[A-Za-z0-9 _\\-ÁáÉéÍíÓóÖöŐőÚúÜüŰű]{1,32}\$", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csapatnév regex", description = "Ez alapján megy majd a validálás"
    )

    val nameBlocklist = StringSettingRef(componentSettingService, component,
        "nameBlocklist", "test, dev", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Tiltott nevek", description = "Tiltott csapatnevek vesszővel elválasztva"
    )

    val racesByDefault = BooleanSettingRef(componentSettingService, component,
        "racesByDefault", true, fieldName = "Alapból versenyzik",
        description = "Ha be van kapcsolva, akkor a csapat automatikusan versenyző státuszban van"
    )

    val selectableByDefault = BooleanSettingRef(componentSettingService, component,
        "selectableByDefault", true, fieldName = "Alapból lehet bele jelentkezni",
        description = "Ha be van kapcsolva, akkor a csapatba automatikusan lehet jelentkezni"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamAdminGroup = ControlGroup(component, "teamAdminGroup", fieldName = "Csapat admin felület")

    val adminTitle = StringSettingRef(componentSettingService, component,
        "adminTitle", "Csapatom kezelése",
        fieldName = "Admin lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val adminMenuDisplayName = StringSettingRef(componentSettingService, component,
        "adminMenuDisplayName", "Csapatom kezelése", serverSideOnly = true,
        fieldName = "Admin menü neve", description = "Ez lesz a neve a menünek"
    )

    val adminMinRole = MinRoleSettingRef(componentSettingService, component,
        "adminMinRole", MinRoleSettingRef.ALL_ROLES_FROM_PRIVILEGED,
        fieldName = "Admin oldal jogosultságai", description = "Melyik roleokkal nyitható meg az admin oldal"
    )

    val togglePermissionEnabled = BooleanSettingRef(componentSettingService, component,
        "togglePermissionEnabled", true, fieldName = "Jogosultság adás állítása",
        description = "Ha be van kapcsolva, akkor lehet jogosultságokat állítani"
    )

    val kickEnabled = BooleanSettingRef(componentSettingService, component,
        "kickEnabled", true, fieldName = "Kidobás gomb",
        description = "Ha be val kapcsolva, akkor csapat vezetők kirakhatnak embereket"
    )

    val promoteLeadershipEnabled = BooleanSettingRef(componentSettingService, component,
        "promoteLeadershipEnabled", true, fieldName = "Jogosultság átadása",
        description = "Ha be val kapcsolva, akkor át lehet adni a vezetőséget másnak"
    )

    val leaderNotes = StringSettingRef(componentSettingService, component,
        "leaderNotes", "", type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Üzenet a CSK-knak",
        description = "A csapat dashboardnál megjelenő üzenet a CSK-k nak. Ha üres nem látszik."
    )

    val showTasks = BooleanSettingRef(componentSettingService, component,
        "showTasks", true, fieldName = "Feladatok mutatása",
        description = "Ha be val kapcsolva, akkor az elvégzendő feladatok látszódnak"
    )

    val showAdvertisedForms = BooleanSettingRef(componentSettingService, component,
        "showAdvertisedForms", true, fieldName = "Formok mutatása",
        description = "Ha be val kapcsolva, akkor a hírdetett flaggel ellátott formokat megjelenítjük a csapatnál"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamDetailsGroup = ControlGroup(component, "teamDetailsGroup", fieldName = "Csapat adatlap")

    val showTeamDetails = BooleanSettingRef(componentSettingService, component,
        "showTeamDetails", false, fieldName = "Csoport részleteinek mutatása",
        description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek"
    )

    val showTeamMembersPublicly = BooleanSettingRef(componentSettingService, component,
        "showTeamMembersPublicly", false, fieldName = "Csoport tagjai publikusak",
        description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek"
    )

    val showTeamScore = BooleanSettingRef(componentSettingService, component,
        "showTeamScore", false, fieldName = "Csoport pontjának kijelzése",
        description = "Ha be val kapcsolva, akkor az adatlapon kint lesz a csoport pontjainak a mutatója"
    )

    val showTeamScoreDetailsButton = BooleanSettingRef(componentSettingService, component,
        "showTeamScoreDetailsButton", false, fieldName = "Csoport részletes pontjaihoz gomb",
        description = "Ha be val kapcsolva, akkor megjelenik egy gomb a csapat részletes pont listájához"
    )

    val showRaceButton = BooleanSettingRef(componentSettingService, component,
        "showRaceButton", false, fieldName = "Mérés gomb látszódik", description = "Ha igaz, látszik a gomb"
    )


    /// -------------------------------------------------------------------------------------------------------------------

    val statGroup = ControlGroup(component, "statGroup", fieldName = "Csapat statisztika")

    val membersStatEnabled = BooleanSettingRef(componentSettingService, component,
        "membersStatEnabled", false, serverSideOnly = true, fieldName = "Tagok számának kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hányan vannak"
    )

    val membersStatHeader = StringSettingRef(componentSettingService, component,
        "membersStatHeader", "Tagok", serverSideOnly = true,
        fieldName = "Tagok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val placeStatEnabled = BooleanSettingRef(componentSettingService, component,
        "placeStatEnabled", false, serverSideOnly = true, fieldName = "Helyezés kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hanyadik a csapat " +
                "(Leaderboard komponens kell hozzá)"
    )

    val placeStatHeader = StringSettingRef(componentSettingService, component,
        "placeStatHeader", "Helyezés", serverSideOnly = true,
        fieldName = "Helyezés fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val scoreStatEnabled = BooleanSettingRef(componentSettingService, component,
        "scoreStatEnabled", false, serverSideOnly = true, fieldName = "Pontszám kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hány pontja van a csapatnak " +
                "(Leaderboard komponens kell hozzá)"
    )

    val scoreStatHeader = StringSettingRef(componentSettingService, component,
        "scoreStatHeader", "Pontszám", serverSideOnly = true,
        fieldName = "Pontszám fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val qrFightStatEnabled = BooleanSettingRef(componentSettingService, component,
        "qrFightStatEnabled", false, serverSideOnly = true, fieldName = "QR Fight kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat által megszerzett pontok és tornyok látszódnak " +
                "(QRFight komponens kell hozzá)"
    )

    val qrTokenStatHeader = StringSettingRef(componentSettingService, component,
        "qrTokenStatHeader", "Megtalált QR kód", serverSideOnly = true,
        fieldName = "QR kódok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val qrTowerStatHeader = StringSettingRef(componentSettingService, component,
        "qrTowerStatHeader", "Megszerzett tornyok", serverSideOnly = true,
        fieldName = "Tornyok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val raceStatEnabled = BooleanSettingRef(componentSettingService, component,
        "raceStatEnabled", false, serverSideOnly = true, fieldName = "Verseny eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy ki és milyen eredménnyel a legjobb " +
                "(Verseny komponens kell hozzá)"
    )

    val raceStatHeader = StringSettingRef(componentSettingService, component,
        "raceStatHeader", "Sörmérés", serverSideOnly = true,
        fieldName = "Verseny fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val riddleStatEnabled = BooleanSettingRef(componentSettingService, component,
        "riddleStatEnable", false, serverSideOnly = true, fieldName = "Riddle eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hogy állnak a ridleökkel " +
                "(Riddle komponens kell hozzá)"
    )

    val riddleStatHeader = StringSettingRef(componentSettingService, component,
        "riddleStatHeader", "Riddleök", serverSideOnly = true,
        fieldName = "Riddle fejléce", description = "Ez lesz a neve a statisztikának"
    )

}
