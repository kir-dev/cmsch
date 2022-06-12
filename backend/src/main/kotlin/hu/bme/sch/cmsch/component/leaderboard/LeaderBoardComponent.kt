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
            minRole,

            leaderboardEnabled,
            leaderboardFrozen
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val leaderboardEnabled = SettingProxy(componentSettingService, component,
        "leaderboardEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Toplista akítv", description = "A toplista leküldésre kerül"
    )

    val leaderboardFrozen = SettingProxy(componentSettingService, component,
        "leaderboardFrozen", "true", type = SettingType.BOOLEAN,
        fieldName = "Toplista befagyasztott", description = "A toplista értéke be van fagyasztva"
    )

}
