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
    componentSettingService,
    "task",
    "/tasks",
    "Feladatok",
    ControlPermissions.PERMISSION_CONTROL_TASKS,
    listOf(TaskEntity::class, TaskCategoryEntity::class, SubmittedTaskEntity::class),
    env
) {

    val taskGroup by SettingGroup(fieldName = "Feladatok")

    final var title by StringSettingRef("Feladatok", fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Feladatok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup by SettingGroup(fieldName = "Nyelvi beállítások")

    var profileRequiredTitle by StringSettingRef("Kötelezően kitöltendő",
        fieldName = "Kötelező feladatok fejléc szövege", description = "Feladatok (PROFILE_REQUIRED) fejléc szövege")

    var profileRequiredMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Kötelező feladatok alatti szöveg",
        description = "Kötelező feladatok (PROFILE_REQUIRED) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs.")

    var regularTitle by StringSettingRef("Feladatok", fieldName = "Feladatok fejléc szövege",
        description = "Feladatok (REGULAR) fejléc szövege")

    var regularMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Feladatok alatti szöveg",
        description = "Feladatok (REGULAR) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs.")

    /// -------------------------------------------------------------------------------------------------------------------

    val exportGroup by SettingGroup(fieldName = "Beadások exportálása")

    var exportEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Endpoint elérhető",
        description = "Ha be van kapcsolva akkor, a /export-tasks endpoint elérhetővé válik")

    var leadOrganizerQuote by StringSettingRef("\"Gratulálunk a csapatoknak!\"\n\n- A főrendezők",
        type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Főrendezők üzenete",
        description = "Ha üres akkor nincs ilyen")

    var logoUrl by StringSettingRef("", type = SettingType.IMAGE_URL,
        fieldName = "Logó URL-je", description = "Az esemény logójának az URL-je")

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var resubmissionEnabled by BooleanSettingRef(fieldName = "Újraküldés lehetséges",
        description = "A lejárati idő végéig újraküldhetőek a beadások, ha már javítva volt, akkor nullázódik a pont.")

    var scoreVisible by BooleanSettingRef(true, serverSideOnly = true, fieldName = "Pontok látszódnak közben",
        description = "A beadási határidő vége előtt is látszik a pont az értékelt feladatokra")

    var scoreVisibleAtAll by BooleanSettingRef(true, serverSideOnly = true, fieldName = "Pontok látszódnak egyáltalán",
        description = "Bármikor látszódjon-e a megszerzett pont (ha ki van " +
                "kapcsolva az nem látszik egyáltalán a feladatnál, csak az összesítésben)")

    var enableViewAudit by BooleanSettingRef(serverSideOnly = true, fieldName = "Feladatok megnyitásának logolása",
        description = "Mentésre kerüljön-e ha valaki megnyit egy feladatot")

}
