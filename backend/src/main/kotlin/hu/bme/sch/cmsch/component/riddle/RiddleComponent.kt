package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["riddle"],
    havingValue = "true",
    matchIfMissing = false
)
class RiddleComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("riddle", "/riddle", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            scoringGroup,
            hintScorePercent,
            saveFailedAttempts
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Riddleök",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Riddleök", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val scoringGroup = SettingProxy(componentSettingService, component,
        "scoringGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Pontozás",
        description = ""
    )

    val hintScorePercent = SettingProxy(componentSettingService, component,
        "hintScorePercent", "100", serverSideOnly = true, type = SettingType.NUMBER,
        fieldName = "Hint pont érték", description = "Ennyi százaléka lesz a hinttel megoldott riddle pont " +
                "értéke a hint élkül megoldottnak"
    )

    val saveFailedAttempts = SettingProxy(componentSettingService, component,
        "saveFailedAttempts", "false", type = SettingType.BOOLEAN,
        fieldName = "Hibás válaszok számának mentése",
        description = "Jelentős plusz erőforrással jár ennek a hazsnálata ha sokan riddleöznek"
    )

}
