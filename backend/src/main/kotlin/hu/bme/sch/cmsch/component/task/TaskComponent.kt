package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
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
            scoreVisibleAtAll,
            enableViewAudit,
        )
    }

    val taskGroup = ControlGroup(component, "taskGroup", fieldName = "Feladatok")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Feladatok", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Feladatok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup = ControlGroup(component, "langGroup", fieldName = "Nyelvi beállítások")

    val profileRequiredTitle = StringSettingRef(componentSettingService, component,
        "profileRequiredTitle", "Kötelezően kitöltendő", type = SettingType.TEXT,
        fieldName = "Kötelező feladatok fejléc szövege", description = "Feladatok (PROFILE_REQUIRED) fejléc szövege"
    )

    val profileRequiredMessage = StringSettingRef(componentSettingService, component,
        "profileRequiredMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Kötelező feladatok alatti szöveg",
        description = "Kötelező feladatok (PROFILE_REQUIRED) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs."
    )

    val regularTitle = StringSettingRef(componentSettingService, component,
        "regularTitle", "Feladatok", type = SettingType.TEXT,
        fieldName = "Feladatok fejléc szövege", description = "Feladatok (REGULAR) fejléc szövege"
    )

    val regularMessage = StringSettingRef(componentSettingService, component,
        "regularMessage", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Feladatok alatti szöveg",
        description = "Feladatok (REGULAR) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val exportGroup = ControlGroup(component, "exportGroup", fieldName = "Beadások exportálása")

    val exportEnabled = BooleanSettingRef(componentSettingService, component,
        "exportEnabled", false, serverSideOnly = true, fieldName = "Endpoint elérhető",
        description = "Ha be van kapcsolva akkor, a /export-tasks endpoint elérhetővé válik"
    )

    val leadOrganizerQuote = StringSettingRef(componentSettingService, component,
        "leadOrganizerQuote", "\"Gratulálunk a csapatoknak!\"\n\n- A főrendezők", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Főrendezők üzenete", description = "Ha üres akkor nincs ilyen"
    )

    val logoUrl = StringSettingRef(componentSettingService, component,
        "logoUrl", "https://", type = SettingType.URL,
        fieldName = "Logó URL-je", description = "Az esemény logójának az URL-je"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = ControlGroup(component, "logicGroup", fieldName = "Működés")

    val resubmissionEnabled = BooleanSettingRef(componentSettingService, component,
        "resubmissionEnabled", false, fieldName = "Újraküldés lehetséges",
        description = "A lejárati idő végéig újraküldhetőek a beadások, ha már javítva volt, akkor nullázódik a pont."
    )

    val scoreVisible = BooleanSettingRef(componentSettingService, component,
        "scoreVisible", true, serverSideOnly = true, fieldName = "Pontok látszódnak közben",
        description = "A beadási határidő vége előtt is látszik a pont az értékelt feladatokra"
    )

    val scoreVisibleAtAll = BooleanSettingRef(componentSettingService, component,
        "scoreVisibleAtAll", true, serverSideOnly = true, fieldName = "Pontok látszódnak egyáltalán",
        description = "Bármikor látszódjon-e a megszerzett pont (ha ki van " +
                "kapcsolva az nem látszik egyáltalán a feladatnál, csak az összesítésben)"
    )

    val enableViewAudit = BooleanSettingRef(componentSettingService, component,
        "enableViewAudit", false, serverSideOnly = true, fieldName = "Feladatok megnyitásának logolása",
        description = "Mentésre kerüljön-e ha valaki megnyit egy feladatot"
    )

}
