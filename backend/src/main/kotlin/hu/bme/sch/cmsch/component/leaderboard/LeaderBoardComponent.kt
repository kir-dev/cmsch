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
    componentSettingService,
    "leaderboard",
    "/leaderboard",
    "Toplista",
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    listOf(),
    env
) {

    val leaderboardGroup by SettingGroup(fieldName = "Toplista")

    final var title by StringSettingRef("Toplista", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Toplista", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(), fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var leaderboardEnabled by BooleanSettingRef(true,
        fieldName = "Toplista aktív", description = "A toplista leküldésre kerül"
    )

    var leaderboardDetailsEnabled by BooleanSettingRef(false,
        fieldName = "Toplista részletek aktív", description = "A részletes toplista leküldésre kerül (Csapatonként)"
    )

    var leaderboardDetailsByCategoryEnabled by BooleanSettingRef(false, fieldName = "Toplista kategória szerint aktív",
        description = "A részletes toplista leküldésre kerül (Kategóriánként)"
    )

    var leaderboardFrozen by BooleanSettingRef(true,
        fieldName = "Toplista befagyasztott", description = "A toplista értéke be van fagyasztva"
    )

    var showScores by BooleanSettingRef(false, fieldName = "Pontok mutatása",
        description = "Ha igaz, akkor látszódnak a pontok, ha hamis, akkor csak a sorrend"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val calcGroup by SettingGroup(fieldName = "Pont számítás")

    var tasksPercent by NumberSettingRef(100, serverSideOnly = true, strictConversion = false,
        fieldName = "Feladatok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    var riddlesPercent by NumberSettingRef(100, serverSideOnly = true, strictConversion = false,
        fieldName = "Riddle szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    var challengesPercent by NumberSettingRef(100, serverSideOnly = true, strictConversion = false,
        fieldName = "Beadások szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    var tokenPercent by NumberSettingRef(100, serverSideOnly = true, strictConversion = false,
        fieldName = "QR Kódok szorzó (%)", description = "100 = 1x, 0 = nem számít bele"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Kijelzés")

    var minScoreToShow by NumberSettingRef(1, fieldName = "Legalább ennyi ponttal", strictConversion = false,
        description = "Legalább ennyi ponttal mutassa a felhasználókat és csoportokat"
    )

    var showUserBoard by BooleanSettingRef(false, fieldName = "Felhasználói toplista mutatása",
        description = "Felhasználói toplista látható legyen-e"
    )

    var maxUserEntryToShow by NumberSettingRef(-1, fieldName = "Toplista sorainak száma", strictConversion = false,
        description = "Hány felhasználót mutasson, -1 = az összeset"
    )

    var showGroupBoard by BooleanSettingRef(false, fieldName = "Csoport toplista mutatása",
        description = "Csoport toplista látható legyen-e"
    )

    var maxGroupEntryToShow by NumberSettingRef(-1, fieldName = "Toplista sorainak száma", strictConversion = false,
        description = "Hány csoportot mutasson, -1 = az összeset"
    )

    var showGroupOfUser by BooleanSettingRef(false, fieldName = "Felhasználó csoportjának kijelzése",
        description = "A felhasználói listán a felhasználó csoportja látható legyen-e"
    )

    var searchEnabled by BooleanSettingRef(false, fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    var showTokenCountByRarity by BooleanSettingRef(false, fieldName = "Begyűjtött tokenek száma ritkaság szerint",
        description = "Legyen-e látható a begyűjtött tokenek száma ritkaság szerint, módosítás után nyomj egy újraszámolást"
    )

    var showTokenMaxCountByRarity by BooleanSettingRef(false, fieldName = "Összes token szám ritkaság szerint",
        description = "Legyen-e látható az összesen begyűjthető tokenek száma ritkaság szerint"
    )

}
