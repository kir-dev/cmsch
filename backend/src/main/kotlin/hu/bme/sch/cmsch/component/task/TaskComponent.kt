package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["task"],
    havingValue = "true",
    matchIfMissing = false
)
class TaskComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "task",
    "/tasks",
    "Feladatok",
    ControlPermissions.PERMISSION_CONTROL_TASKS,
    listOf(TaskEntity::class, TaskCategoryEntity::class, SubmittedTaskEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            taskGroup,
            title, menuDisplayName, minRole,

            langGroup,
            profileRequiredTitle,
            profileRequiredMessage,
            regularTitle,
            regularMessage,

            exportGroup,
            exportEnabled,
            leadOrganizerQuote,
            logoUrl,

            logicGroup,
            resubmissionEnabled,
            scoreVisible,
            scoreVisibleAtAll
        )
    }

    val taskGroup = SettingProxy(componentSettingService, component,
        "taskGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Feladatok",
        description = ""
    )

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
        fieldName = "Nyelvi beállítások",
        description = ""
    )

    val profileRequiredTitle = SettingProxy(componentSettingService, component,
        "profileRequiredTitle", "Kötelezően kitöltendő", type = SettingType.TEXT,
        fieldName = "Kötelező feladatok fejléc szövege", description = "Feladatok (PROFILE_REQUIRED) fejléc szövege"
    )

    val profileRequiredMessage = SettingProxy(componentSettingService, component,
        "profileRequiredMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Kötelező feladatok alatti szöveg",
        description = "Kötelező feladatok (PROFILE_REQUIRED) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs."
    )

    val regularTitle = SettingProxy(componentSettingService, component,
        "regularTitle", "Feladatok", type = SettingType.TEXT,
        fieldName = "Feladatok fejléc szövege", description = "Feladatok (REGULAR) fejléc szövege"
    )

    val regularMessage = SettingProxy(componentSettingService, component,
        "regularMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Feladatok alatti szöveg",
        description = "Feladatok (REGULAR) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val exportGroup = SettingProxy(componentSettingService, component,
        "exportGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Beadások exportálása",
        description = ""
    )

    val exportEnabled = SettingProxy(componentSettingService, component,
        "exportEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Endpoint elérhető", description = "Ha be van kapcsolva akkor, a /export-tasks endpoint elérhetővé válik"
    )

    val leadOrganizerQuote = SettingProxy(componentSettingService, component,
        "leadOrganizerQuote", "\"Gratulálunk a csapatoknak!\"\n\n- A főrendezők",
        type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Főrendezők üzenete", description = "Ha üres akkor nincs ilyen"
    )

    val logoUrl = SettingProxy(componentSettingService, component,
        "logoUrl", "https://", type = SettingType.URL,
        fieldName = "Logó URL-je", description = "Az esemény logójának az URL-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingProxy(componentSettingService, component,
        "logicGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Működés",
        description = ""
    )

    val resubmissionEnabled = SettingProxy(componentSettingService, component,
        "resubmissionEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Újraküldés lehetséges",
        description = "A lejárati idő végéig újraküldhetőek a beadások, ha már javítva volt, akkor nullázódik a pont."
    )

    val scoreVisible = SettingProxy(componentSettingService, component,
        "scoreVisible", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Pontok látszódnak közben",
        description = "A beadási határidő vége előtt is látszik a pont az értékelt feladatokra"
    )

    val scoreVisibleAtAll = SettingProxy(componentSettingService, component,
        "scoreVisibleAtAll", "true", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Pontok látszódnak egyáltalán", description = "Bármikor látszódjon-e a megszerzett pont (ha ki van " +
                "kapcsolva az nem látszik egyáltalán a feladatnál, csak az összesítésben)"
    )

}
