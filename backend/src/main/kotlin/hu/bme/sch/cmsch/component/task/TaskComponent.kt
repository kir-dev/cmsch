package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.task"])
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
        fieldName = "Jogosultságok", description = "Mely szerepkörökkel nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val langGroup by SettingGroup(fieldName = "Nyelvi beállítások")

    var profileRequiredTitle by StringSettingRef("Kötelezően kitöltendő",
        fieldName = "Kötelező feladatok fejléc szövege", description = "A kötelező (PROFILE_REQUIRED) feladatok fejléce")

    var profileRequiredMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Kötelező feladatok alatti szöveg",
        description = "A kötelező (PROFILE_REQUIRED) feladatok fejléce alatt megjelenő szöveg. Ha üres, nem jelenik meg.")

    var regularTitle by StringSettingRef("Feladatok", fieldName = "Feladatok fejléc szövege",
        description = "A normál (REGULAR) feladatok fejléce")

    var regularMessage by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Feladatok alatti szöveg",
        description = "A normál (REGULAR) feladatok fejléce alatt megjelenő szöveg. Ha üres, nem jelenik meg.")

    /// -------------------------------------------------------------------------------------------------------------------

    val exportGroup by SettingGroup(fieldName = "Beadások exportálása")

    var exportEnabled by BooleanSettingRef(serverSideOnly = true, fieldName = "Endpoint elérhető",
        description = "Bekapcsolt állapotban az /export-tasks végpont elérhetővé válik")

    var leadOrganizerQuote by StringSettingRef("\"Gratulálunk a csapatoknak!\"\n\n- A főrendezők",
        type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "Főrendezők üzenete",
        description = "A főrendezők üzenete az exportált dokumentumban. Ha üres, nem jelenik meg.")

    var logoUrl by StringSettingRef("", type = SettingType.IMAGE_URL,
        fieldName = "Logó URL-je", description = "Az esemény logójának URL-je")

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var resubmissionEnabled by BooleanSettingRef(fieldName = "Újraküldés lehetséges",
        description = "A határidő végéig a feladatok újraküldhetőek. Ha már értékelve volt, a pontszám nullázódik.")

    var scoreVisible by BooleanSettingRef(true, serverSideOnly = true, fieldName = "Pontok látszódnak közben",
        description = "A beadási határidő vége előtt is láthatóak az értékelt feladatok pontszámai")

    var scoreVisibleAtAll by BooleanSettingRef(true, serverSideOnly = true, fieldName = "Pontok látszódnak egyáltalán",
        description = "Bármikor látható legyen-e a megszerzett pontszám (kikapcsolva csak az összesítésben jelenik meg)")

    var enableViewAudit by BooleanSettingRef(serverSideOnly = true, fieldName = "Feladatok megnyitásának logolása",
        description = "Bekapcsolt állapotban a rendszer menti, ha egy felhasználó megnyit egy feladatot")

}
