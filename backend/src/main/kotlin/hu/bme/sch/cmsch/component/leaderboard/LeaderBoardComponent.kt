package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("leaderboard", "/leaderboard", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, minRole, menuDisplayName,

            logicGroup,
            leaderboardEnabled,
            leaderboardFrozen,
            showScores,

            displayGroup,
            minScoreToShow,
            showUserBoard,
            maxUserEntryToShow,
            showGroupBoard,
            maxGroupEntryToShow
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Programok",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Programok", serverSideOnly = true,
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
        fieldName = "Toplista akítv", description = "A toplista leküldésre kerül"
    )

    val leaderboardFrozen = SettingProxy(componentSettingService, component,
        "leaderboardFrozen", "true", type = SettingType.BOOLEAN,
        fieldName = "Toplista befagyasztott", description = "A toplista értéke be van fagyasztva"
    )

    val showScores = SettingProxy(componentSettingService, component,
        "showScores", "false", type = SettingType.BOOLEAN,
        fieldName = "Pontok mutatása",
        description = "Ha igaz, akkor látzódnak a pontok, ha hamis, akkor csak a sorrend"
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


}
