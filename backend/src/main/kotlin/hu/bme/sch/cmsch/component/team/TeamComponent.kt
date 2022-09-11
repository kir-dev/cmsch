package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.*
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
            title, menuDisplayName, minRole,

            langGroup,


            teamListGroup,
            showGroupsAtAll,
            showEmptyGroups,
            showNotRacingGroups,
            sortByName,

            teamCreateGroup,
            creationEnabled,
            joinEnabled,
            grantPrivilegedRole,
            grantAttendeeRole,

            teamDetailsGroup,
            showTeamDetails,
            showTeamMembersPublicly,
            showTeamScore,
            showTeamScoreDetailsButton,
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Feladatok",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Feladatok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup = SettingProxy(componentSettingService, component,
        "langGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Nyelvi bellítások",
        description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val teamListGroup = SettingProxy(componentSettingService, component,
        "teamListGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat lista",
        description = ""
    )

    val showGroupsAtAll = SettingProxy(componentSettingService, component,
        "showGroupsAtAll", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Csapat lista kijelzése", description = "Ha ki van kapcsolva a kkor a csapat lista nincs leküldve"
    )

    val showEmptyGroups = SettingProxy(componentSettingService, component,
        "showEmptyGroups", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Üres csapat kijelzése", description = "A tagnélküli csapatok is látszódjanak-e"
    )

    val showNotRacingGroups = SettingProxy(componentSettingService, component,
        "showNotRacingGroups", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Nem versenyző csapatok kijelzése", description = "Azoknak is a kijelzése akik nem versenyeznek"
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

    /// -------------------------------------------------------------------------------------------------------------------

    val teamDetailsGroup = SettingProxy(componentSettingService, component,
        "teamDetailsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csapat adatlap",
        description = ""
    )

    val showTeamDetails = SettingProxy(componentSettingService, component,
        "showTeamDetails", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Csoport részleteinek mutatása", description = "Ha be val kapcsolva, akkor a csapatok adatai megtekinthetőek"
    )

    val showTeamMembersPublicly = SettingProxy(componentSettingService, component,
        "showTeamMembersPublicly", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
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
