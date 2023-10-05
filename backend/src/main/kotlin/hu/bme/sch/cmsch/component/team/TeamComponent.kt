package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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
                adminMenuDisplayName.getValue(), "/team-admin", 0,
                visible = false, subMenu = false, external = false
            ))
        }
        return result
    }

    /// -------------------------------------------------------------------------------------------------------------------

    val myTeamGroup = SettingProxy(componentSettingService, component,
        "myTeamGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapatom",
        description = ""
    )

    val myTitle = SettingProxy(componentSettingService, component,
        "myTitle", "Csapatom",
        fieldName = "Csapatom lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val myMenuDisplayName = SettingProxy(componentSettingService, component,
        "myMenuDisplayName", "Csapatom", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    val myMinRole = MinRoleSettingProxy(componentSettingService, component,
        "myMinRole", MinRoleSettingProxy.ALL_ROLES_FROM_ATTENDEE,
        fieldName = "Csapatom menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamListGroup = SettingProxy(componentSettingService, component,
        "teamListGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat lista",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Csapatok",
        fieldName = "Csapatok lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Csapatok", serverSideOnly = true,
        fieldName = "Csapatok menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Csapatok menü jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val showTeamsAtAll = SettingProxy(componentSettingService, component,
        "showTeamsAtAll", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Csapat lista kijelzése", description = "Ha ki van kapcsolva a akkor a csapat lista nincs leküldve"
    )

    val showNotRacingTeams = SettingProxy(componentSettingService, component,
        "showNotRacingTeams", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Nem versenyző csapatok kijelzése", description = "Azoknak is a kijelzése akik nem versenyeznek"
    )

    val showNotManualTeams = SettingProxy(componentSettingService, component,
        "showNotManualTeams", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Admin által nevezett csapatok is látszanak", description = "Azoknak is a kijelzése akiket az admin panelről neveztek"
    )

    val sortByName = SettingProxy(componentSettingService, component,
        "sortByName", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Név alapján rendezés", description = "Ha igaz, akkor ABC sorrendben vannak kiírva, ha hamis akkor pont alapján"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamCreateGroup = SettingProxy(componentSettingService, component,
        "teamCreateGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat létrehozás",
        description = ""
    )

    val createTitle = SettingProxy(componentSettingService, component,
        "createTitle", "Csapat készítés",
        fieldName = "Csapat készítés lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val createMenuDisplayName = SettingProxy(componentSettingService, component,
        "createMenuDisplayName", "Csapat készítés", serverSideOnly = true,
        fieldName = "Csapat készítés neve", description = "Ez lesz a neve a menünek"
    )

    val createMinRole = MinRoleSettingProxy(componentSettingService, component,
        "createMinRole", RoleType.BASIC.name,
        fieldName = "Csapat készítés menü jogosultságai", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val teamCreationTopMessage = SettingProxy(componentSettingService, component,
        "teamCreationTopMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csapat készítés felső szöveg", description = "Ha üres, akkor nincs ilyen"
    )

    val creationEnabled = SettingProxy(componentSettingService, component,
        "creationEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport készítés engedélyezve", description = "Ha igaz, lehet csapatot készíteni"
    )

    val teamEditTitle = SettingProxy(componentSettingService, component,
        "teamEditTitle", "Csapat szerkesztése",
        fieldName = "Csoport adatainak szerkesztés lap címe", description = "A csoport szerkesztési lapjának tetején megjelenő címe"
    )

    val teamEditTopMessage = SettingProxy(componentSettingService, component,
        "teamEditTopMessage", "Adj meg leírást és logót!", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Csoport szerkesztés felső szöveg", description = "Ha üres, akkor nincs ilyen"
    )

    val teamEditEnabled = SettingProxy(componentSettingService, component,
        "teamEditEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Csoport szerkesztés engedélyezése", description = "Ha igaz, lehet a csapat leírását szerkeszteni"
    )

    val teamLogoUploadEnabled = SettingProxy(componentSettingService, component,
        "teamLogoUploadEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport logó feltöltés engedélyezése", description = "Ha igaz, lehet a vezetőknek logót feltölteni"
    )

    val joinEnabled = SettingProxy(componentSettingService, component,
        "joinEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csatlakozás engedélyezve", description = "Ha igaz, lehet csapathoz csatlakozni"
    )

    val leaveEnabled = SettingProxy(componentSettingService, component,
        "leaveEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csatlakozás engedélyezve", description = "Ha igaz, lehet ki lehet lépni csapatból"
    )

    val grantPrivilegedRole = SettingProxy(componentSettingService, component,
        "grantPrivilegedRole", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "PRIVILEGED jog a csapat készítőjének", description = "Ha be val kapcsolva, akkor a csapat készítője PRIVILEGED jogot kap"
    )

    val grantAttendeeRole = SettingProxy(componentSettingService, component,
        "grantAttendeeRole", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "ATTENDEE jog a csapattagoknak", description = "Ha be val kapcsolva, akkor a csapat tagjai ATTENDEE jogot kapnak (onnantól él, hogy be lett kapcsolva)"
    )

    val nameRegex = SettingProxy(componentSettingService, component,
        "nameRegex", "^[A-Za-z0-9 _\\-ÁáÉéÍíÓóÖöŐőÚúÜüŰű]{1,32}\$", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Csapatnév regex", description = "Ez alapján megy majd a validálás"
    )

    val nameBlocklist = SettingProxy(componentSettingService, component,
        "nameBlocklist", "test, dev", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Tiltott nevek", description = "Tiltott csapatnevek vesszővel elválasztva"
    )

    val racesByDefault = SettingProxy(componentSettingService, component,
        "racesByDefault", "true", type = SettingType.BOOLEAN,
        fieldName = "Alapból versenyzik", description = "Ha be van kapcsolva, akkor a csapat automatikusan versenyző státuszban van"
    )

    val selectableByDefault = SettingProxy(componentSettingService, component,
        "selectableByDefault", "true", type = SettingType.BOOLEAN,
        fieldName = "Alapból lehet bele jelentkezni", description = "Ha be van kapcsolva, akkor a csapatba automatikusan lehet jelentkezni"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamAdminGroup = SettingProxy(componentSettingService, component,
        "teamAdminGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat admin felület",
        description = ""
    )

    val adminTitle = SettingProxy(componentSettingService, component,
        "adminTitle", "Csapatom kezelése",
        fieldName = "Admin lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    val adminMenuDisplayName = SettingProxy(componentSettingService, component,
        "adminMenuDisplayName", "Csapatom kezelése", serverSideOnly = true,
        fieldName = "Admin menü neve", description = "Ez lesz a neve a menünek"
    )

    val adminMinRole = MinRoleSettingProxy(componentSettingService, component,
        "adminMinRole", MinRoleSettingProxy.ALL_ROLES_FROM_PRIVILEGED,
        fieldName = "Admin oldal jogosultságai", description = "Melyik roleokkal nyitható meg az admin oldal"
    )

    val togglePermissionEnabled = SettingProxy(componentSettingService, component,
        "togglePermissionEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Jogosultság adás állítása", description = "Ha be van kapcsolva, akkor lehet jogosultságokat állítani"
    )

    val kickEnabled = SettingProxy(componentSettingService, component,
        "kickEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Kidobás gomb", description = "Ha be val kapcsolva, akkor csapat vezetők kirakhatnak embereket"
    )

    val promoteLeadershipEnabled = SettingProxy(componentSettingService, component,
        "promoteLeadershipEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Jogosultság átadása", description = "Ha be val kapcsolva, akkor át lehet adni a vezetőséget másnak"
    )

    val leaderNotes = SettingProxy(componentSettingService, component,
        "leaderNotes", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Üzenet a CSK-knak", description = "A csapat dashboardnál megjelenő üzenet a CSK-k nak. Ha üres nem látszik."
    )

    val showTasks = SettingProxy(componentSettingService, component,
        "showTasks", "true", type = SettingType.BOOLEAN,
        fieldName = "Feladatok mutatása", description = "Ha be val kapcsolva, akkor az elvégzendő feladatok látszódnak"
    )

    val showAdvertisedForms = SettingProxy(componentSettingService, component,
        "showAdvertisedForms", "true", type = SettingType.BOOLEAN,
        fieldName = "Formok mutatása", description = "Ha be val kapcsolva, akkor a hírdetett flaggel " +
                "ellátott formokat megjelenítjük a csapatnál"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamDetailsGroup = SettingProxy(componentSettingService, component,
        "teamDetailsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat adatlap",
        description = ""
    )

    val showTeamDetails = SettingProxy(componentSettingService, component,
        "showTeamDetails", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport részleteinek mutatása", description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek"
    )

    val showTeamMembersPublicly = SettingProxy(componentSettingService, component,
        "showTeamMembersPublicly", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport tagjai publikusak", description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek"
    )

    val showTeamScore = SettingProxy(componentSettingService, component,
        "showTeamScore", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport pontjának kijelzése", description = "Ha be val kapcsolva, akkor az adatlapon kint lesz a csoport pontjainak a mutatója"
    )

    val showTeamScoreDetailsButton = SettingProxy(componentSettingService, component,
        "showTeamScoreDetailsButton", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport részletes pontjaihoz gomb", description = "Ha be val kapcsolva, akkor megjelenik egy gomb a csapat részletes pont listájához"
    )

    val showRaceButton = SettingProxy(componentSettingService, component,
        "showRaceButton", "false", type = SettingType.BOOLEAN,
        fieldName = "Mérés gomb látszódik", description = "Ha igaz, látszik a gomb"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val statGroup = SettingProxy(componentSettingService, component,
        "statGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat statisztika",
        description = ""
    )

    val membersStatEnabled = SettingProxy(componentSettingService, component,
        "membersStatEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Tagok számának kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hányan vannak"
    )

    val membersStatHeader = SettingProxy(componentSettingService, component,
        "membersStatHeader", "Tagok", serverSideOnly = true,
        fieldName = "Tagok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val placeStatEnabled = SettingProxy(componentSettingService, component,
        "placeStatEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Helyezés kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hanyadik a csapat " +
                "(Leaderboard komponens kell hozzá)"
    )

    val placeStatHeader = SettingProxy(componentSettingService, component,
        "placeStatHeader", "Helyezés", serverSideOnly = true,
        fieldName = "Helyezés fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val scoreStatEnabled = SettingProxy(componentSettingService, component,
        "scoreStatEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Pontszám kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hány pontja van a csapatnak " +
                "(Leaderboard komponens kell hozzá)"
    )

    val scoreStatHeader = SettingProxy(componentSettingService, component,
        "scoreStatHeader", "Pontszám", serverSideOnly = true,
        fieldName = "Pontszám fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val qrFightStatEnabled = SettingProxy(componentSettingService, component,
        "qrFightStatEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "QR Fight kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat által megszerzett pontok és tornyok látszódnak " +
                "(QRFight komponens kell hozzá)"
    )

    val qrTokenStatHeader = SettingProxy(componentSettingService, component,
        "qrTokenStatHeader", "Megtalált QR kód", serverSideOnly = true,
        fieldName = "QR kódok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val qrTowerStatHeader = SettingProxy(componentSettingService, component,
        "qrTowerStatHeader", "Megszerzett tornyok", serverSideOnly = true,
        fieldName = "Tornyok fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val raceStatEnabled = SettingProxy(componentSettingService, component,
        "raceStatEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Verseny eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy ki és milyen eredménnyel a legjobb " +
                "(Verseny komponens kell hozzá)"
    )

    val raceStatHeader = SettingProxy(componentSettingService, component,
        "raceStatHeader", "Sörmérés", serverSideOnly = true,
        fieldName = "Verseny fejléce", description = "Ez lesz a neve a statisztikának"
    )

    val riddleStatEnabled = SettingProxy(componentSettingService, component,
        "riddleStatEnable", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Riddle eredmény kijelzése",
        description = "Ha be val kapcsolva, akkor a csapat mutatásánál látszik, hogy hogy állnak a ridleökkel " +
                "(Riddle komponens kell hozzá)"
    )

    val riddleStatHeader = SettingProxy(componentSettingService, component,
        "riddleStatHeader", "Riddleök", serverSideOnly = true,
        fieldName = "Riddle fejléce", description = "Ez lesz a neve a statisztikának"
    )

}
