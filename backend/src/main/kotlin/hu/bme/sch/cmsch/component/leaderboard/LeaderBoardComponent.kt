package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["leaderboard"],
    havingValue = "true",
    matchIfMissing = false
)
class LeaderBoardComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "leaderboard",
    "/leaderboard",
    "Toplista",
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            leaderboardGroup,
            title, minRole, menuDisplayName,

            logicGroup,
            leaderboardEnabled,
            leaderboardDetailsEnabled,
            leaderboardDetailsByCategoryEnabled,
            leaderboardFrozen,
            showScores,

            calcGroup,
            tasksPercent,
            riddlesPercent,
            challengesPercent,
            tokenPercent,

            displayGroup,
            minScoreToShow,
            showUserBoard,
            maxUserEntryToShow,
            showGroupBoard,
            maxGroupEntryToShow,
            showGroupOfUser,
            searchEnabled
        )
    }

    val leaderboardGroup = SettingProxy(componentSettingService, component,
        "leaderboardGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Toplista",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Toplista",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Toplista", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingProxy(componentSettingService, component,
        "logicGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Működés",
        description = ""
    )

    val leaderboardEnabled = SettingProxy(componentSettingService, component,
        "leaderboardEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Toplista aktív", description = "A toplista leküldésre kerül"
    )

    val leaderboardDetailsEnabled = SettingProxy(componentSettingService, component,
        "leaderboardDetailsEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Toplista részletek aktív", description = "A részletes toplista leküldésre kerül (Csapatonként)"
    )

    val leaderboardDetailsByCategoryEnabled = SettingProxy(componentSettingService, component,
        "leaderboardDetailsByCategoryEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Toplista kategória szerint aktív", description = "A részletes toplista leküldésre kerül (Kategóriánként)"
    )

    val leaderboardFrozen = SettingProxy(componentSettingService, component,
        "leaderboardFrozen", "true", type = SettingType.BOOLEAN,
        fieldName = "Toplista befagyasztott", description = "A toplista értéke be van fagyasztva"
    )

    val showScores = SettingProxy(componentSettingService, component,
        "showScores", "false", type = SettingType.BOOLEAN,
        fieldName = "Pontok mutatása",
        description = "Ha igaz, akkor látszódnak a pontok, ha hamis, akkor csak a sorrend"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val calcGroup = SettingProxy(componentSettingService, component,
        "calcGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Pont számítás",
        description = ""
    )

    val tasksPercent = SettingProxy(componentSettingService, component,
        "tasksPercent", "100", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Feladatok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val riddlesPercent = SettingProxy(componentSettingService, component,
        "riddlesPercent", "100", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Riddle szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val challengesPercent = SettingProxy(componentSettingService, component,
        "challengesPercent", "100", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "Beadások szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val tokenPercent = SettingProxy(componentSettingService, component,
        "tokenPercent", "100", type = SettingType.NUMBER, serverSideOnly = true,
        fieldName = "QR Kódok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingProxy(componentSettingService, component,
        "displayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kijelzés",
        description = ""
    )

    val minScoreToShow = SettingProxy(componentSettingService, component,
        "minScoreToShow", "1", type = SettingType.NUMBER,
        fieldName = "Legalább ennyi ponttal",
        description = "Legalább ennyi ponttal mutassa a felhasználókat és csoportokat"
    )

    val showUserBoard = SettingProxy(componentSettingService, component,
        "showUserBoard", "false", type = SettingType.BOOLEAN,
        fieldName = "Felhasználói toplista mutatása",
        description = "Felhasználói toplista látható legyen-e"
    )

    val maxUserEntryToShow = SettingProxy(componentSettingService, component,
        "maxUserEntryToShow", "-1", type = SettingType.NUMBER,
        fieldName = "Toplista sorainak száma",
        description = "Hány felhasználót mutasson, -1 = az összeset"
    )

    val showGroupBoard = SettingProxy(componentSettingService, component,
        "showGroupBoard", "false", type = SettingType.BOOLEAN,
        fieldName = "Csoport toplista mutatása",
        description = "Csoport toplista látható legyen-e"
    )

    val maxGroupEntryToShow = SettingProxy(componentSettingService, component,
        "maxGroupEntryToShow", "-1", type = SettingType.NUMBER,
        fieldName = "Toplista sorainak száma",
        description = "Hány csoportot mutasson, -1 = az összeset"
    )

    val showGroupOfUser = SettingProxy(componentSettingService, component,
        "showGroupOfUser", "false", type = SettingType.BOOLEAN,
        fieldName = "Felhasználó csoportjának kijelzése",
        description = "A felhasználói listán a felhasználó csoportja látható legyen-e"
    )

    val searchEnabled = SettingProxy(componentSettingService, component,
        "searchEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

}
