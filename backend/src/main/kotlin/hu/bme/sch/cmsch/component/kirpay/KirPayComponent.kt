package hu.bme.sch.cmsch.component.kirpay

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.kirpay"])
class KirPayComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "kirpay",
    "/kirpay-leaderboard",
    "Kir-Pay Toplista",
    ControlPermissions.PERMISSION_CONTROL_LEADERBOARD,
    listOf(),
    env
) {

    val leaderboardGroup by SettingGroup(fieldName = "Toplista")

    final var title by StringSettingRef("Kir-Pay Toplista",
        fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Kir-Pay Toplista", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val connectionGroup by SettingGroup(fieldName = "Kapcsolat")

    var kirPayBackendUrl by StringSettingRef("http://localhost:3001/v1/api/admin", serverSideOnly = true,
        fieldName = "Kir-Pay backend URL", description = "A Kir-Pay backend admin API URL-je")

    var kirPayBackendToken by StringSettingRef(serverSideOnly = true, fieldName = "Kir-Pay backend token",
        description = "Basic auth token a Kir-Pay admin API-hoz")

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup by SettingGroup(fieldName = "Kijelzés")

    var leaderboardEnabled by BooleanSettingRef(true,
        fieldName = "Toplista aktív", description = "A toplista leküldésre kerül")

    var leaderboardMaxEntries by NumberSettingRef(50, fieldName = "Toplista sorainak száma",
        description = "Hány felhasználót mutasson, -1 = az összeset")

    var kirPayCurrency by StringSettingRef("JMF", fieldName = "A megjelenített Kir-Pay valuta")

}
