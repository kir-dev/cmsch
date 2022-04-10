package hu.bme.sch.cmsch.component.achievement

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["achievement"],
    havingValue = "true",
    matchIfMissing = false
)
class AchievementComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("achievement", "/tasks", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            leaderboardEnabled,
            leaderboardFrozen
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Profil",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Profil", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
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
