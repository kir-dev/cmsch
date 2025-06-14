package hu.bme.sch.cmsch.component.leaderboard

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
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
            searchEnabled,
            showTokenCountByRarity,
            showTokenMaxCountByRarity
        )
    }

    val leaderboardGroup = SettingGroup(component, "leaderboardGroup", fieldName = "Toplista")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Toplista", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Toplista", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingGroup(component, "logicGroup", fieldName = "Működés")

    val leaderboardEnabled = BooleanSettingRef(componentSettingService, component,
        "leaderboardEnabled", true,
        fieldName = "Toplista aktív", description = "A toplista leküldésre kerül"
    )

    val leaderboardDetailsEnabled = BooleanSettingRef(componentSettingService, component,
        "leaderboardDetailsEnabled", false,
        fieldName = "Toplista részletek aktív", description = "A részletes toplista leküldésre kerül (Csapatonként)"
    )

    val leaderboardDetailsByCategoryEnabled = BooleanSettingRef(componentSettingService,
        component, "leaderboardDetailsByCategoryEnabled", false, fieldName = "Toplista kategória szerint aktív",
        description = "A részletes toplista leküldésre kerül (Kategóriánként)"
    )

    val leaderboardFrozen = BooleanSettingRef(componentSettingService, component,
        "leaderboardFrozen", true,
        fieldName = "Toplista befagyasztott", description = "A toplista értéke be van fagyasztva"
    )

    val showScores = BooleanSettingRef(componentSettingService, component,
        "showScores", false, fieldName = "Pontok mutatása",
        description = "Ha igaz, akkor látszódnak a pontok, ha hamis, akkor csak a sorrend"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val calcGroup = SettingGroup(component, "calcGroup", fieldName = "Pont számítás")

    val tasksPercent = NumberSettingRef(componentSettingService, component,
        "tasksPercent", 100, serverSideOnly = true, strictConversion = false,
        fieldName = "Feladatok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val riddlesPercent = NumberSettingRef(componentSettingService, component,
        "riddlesPercent", 100, serverSideOnly = true, strictConversion = false,
        fieldName = "Riddle szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val challengesPercent = NumberSettingRef(componentSettingService, component,
        "challengesPercent", 100, serverSideOnly = true, strictConversion = false,
        fieldName = "Beadások szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    val tokenPercent = NumberSettingRef(componentSettingService, component,
        "tokenPercent", 100, serverSideOnly = true, strictConversion = false,
        fieldName = "QR Kódok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingGroup(component, "displayGroup", fieldName = "Kijelzés")

    val minScoreToShow = NumberSettingRef(componentSettingService, component,
        "minScoreToShow", 1, fieldName = "Legalább ennyi ponttal", strictConversion = false,
        description = "Legalább ennyi ponttal mutassa a felhasználókat és csoportokat"
    )

    val showUserBoard = BooleanSettingRef(componentSettingService, component,
        "showUserBoard", false, fieldName = "Felhasználói toplista mutatása",
        description = "Felhasználói toplista látható legyen-e"
    )

    val maxUserEntryToShow = NumberSettingRef(componentSettingService, component,
        "maxUserEntryToShow", -1, fieldName = "Toplista sorainak száma", strictConversion = false,
        description = "Hány felhasználót mutasson, -1 = az összeset"
    )

    val showGroupBoard = BooleanSettingRef(componentSettingService, component,
        "showGroupBoard", false, fieldName = "Csoport toplista mutatása",
        description = "Csoport toplista látható legyen-e"
    )

    val maxGroupEntryToShow = NumberSettingRef(componentSettingService, component,
        "maxGroupEntryToShow", -1, fieldName = "Toplista sorainak száma", strictConversion = false,
        description = "Hány csoportot mutasson, -1 = az összeset"
    )

    val showGroupOfUser = BooleanSettingRef(componentSettingService, component,
        "showGroupOfUser", false, fieldName = "Felhasználó csoportjának kijelzése",
        description = "A felhasználói listán a felhasználó csoportja látható legyen-e"
    )

    val searchEnabled = BooleanSettingRef(componentSettingService, component,
        "searchEnabled", false, fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    val showTokenCountByRarity = BooleanSettingRef(componentSettingService, component,
        "showTokenCountByRarity", false, fieldName = "Begyűjtött tokenek száma ritkaság szerint",
        description = "Legyen-e látható a begyűjtött tokenek száma ritkaság szerint, módosítás után nyomj egy újraszámolást"
    )

    val showTokenMaxCountByRarity = BooleanSettingRef(componentSettingService, component,
        "showTokenMaxCountByRarity", false, fieldName = "Összes token szám ritkaság szerint",
        description = "Legyen-e látható az összesen begyűjthető tokenek száma ritkaság szerint"
    )

}
