package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.app.MenuSettingItem
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("team", "/teams", componentSettingService, env) {

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
            joinEnabled,
            grantPrivilegedRole,
            grantAttendeeRole,
            nameBlocklist,
            racesByDefault,
            selectableByDefault,

            teamAdminGroup,
            adminTitle,
            adminMenuDisplayName,
            adminMinRole,
            togglePermissionEnabled,
            kickEnabled,

            teamDetailsGroup,
            showTeamDetails,
            showTeamMembersPublicly,
            showTeamScore,
            showTeamScoreDetailsButton
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
        fieldName = "Csapat lista kijelzése", description = "Ha ki van kapcsolva a kkor a csapat lista nincs leküldve"
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

    val joinEnabled = SettingProxy(componentSettingService, component,
        "joinEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csatlakozás engedélyezve", description = "Ha igaz, lehet csapathoz csatlakozni"
    )

    val leaveEnabled = SettingProxy(componentSettingService, component,
        "leaveEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Csatlakozás engedélyezve", description = "Ha igaz, lehet ki lehet lépni csataból"
    )

    val grantPrivilegedRole = SettingProxy(componentSettingService, component,
        "grantPrivilegedRole", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "PRIVILEGED jog a csapat készítőjének", description = "Ha be val kapcsolva, akkor a csapat készítője PRIVILEGED jogot kap"
    )

    val grantAttendeeRole = SettingProxy(componentSettingService, component,
        "grantAttendeeRole", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "ATTENDEE jog a cspattagoknak", description = "Ha be val kapcsolva, akkor a csapat tagjai ATTENDEE jogot kapnak (onnantól él, hogy be lett kapcsolva)"
    )

    val nameBlocklist = SettingProxy(componentSettingService, component,
        "nameBlocklist", "test, dev", type = SettingType.TEXT,
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
        fieldName = "Jogosultság adás álltása", description = "Ha be val kapcsolva, akkor lehet jogosultásokat állítani"
    )

    val kickEnabled = SettingProxy(componentSettingService, component,
        "kickEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Kidobás gomb", description = "Ha be val kapcsolva, akkor csapat vezetők kirakhatnak embereket"
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

}
