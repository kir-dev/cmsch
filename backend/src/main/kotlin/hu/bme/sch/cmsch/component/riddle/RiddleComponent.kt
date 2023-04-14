package hu.bme.sch.cmsch.component.riddle

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.race.RaceCategoryEntity
import hu.bme.sch.cmsch.component.race.RaceRecordEntity
import hu.bme.sch.cmsch.service.ControlPermissions
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
) : ComponentBase(
    "riddle",
    "/riddle",
    "Riddleök",
    ControlPermissions.PERMISSION_CONTROL_RIDDLE,
    listOf(RiddleEntity::class, RaceCategoryEntity::class, RiddleMappingEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            riddleGroup,
            title, menuDisplayName, minRole,

            scoringGroup,
            hintScorePercent,
            saveFailedAttempts,

            answerGroup,
            ignoreCase,
            ignoreWhitespace,
            ignoreAccent
        )
    }

    val riddleGroup = SettingProxy(componentSettingService, component,
        "riddleGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Riddleök",
        description = ""
    )

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
        description = "Jelentős plusz erőforrással jár ennek a használata ha sokan riddleöznek"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val answerGroup = SettingProxy(componentSettingService, component,
        "answerGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Válaszok ellenőrzése",
        description = "A transzformációt a beküldött és a riddleben található megoldásra is futtatjuk"
    )

    val ignoreCase = SettingProxy(componentSettingService, component,
        "ignoreCase", "true", type = SettingType.BOOLEAN,
        fieldName = "Kis/nagy betű ignorálása",
        description = "A válaszoknál nem számít a kis- és nagybetű"
    )

    val ignoreWhitespace = SettingProxy(componentSettingService, component,
        "ignoreWhitespace", "false", type = SettingType.BOOLEAN,
        fieldName = "Elválasztás ignorálása",
        description = "A válaszoknál nem számít a szavak elválasztása (szóköz, kötőjel, &, +, vessző)"
    )

    val ignoreAccent = SettingProxy(componentSettingService, component,
        "ignoreAccent", "false", type = SettingType.BOOLEAN,
        fieldName = "Ékezetek ignorálása",
        description = "A válaszoknál nem számítanak az ékezetek (áéíóöőúüű)"
    )

}
