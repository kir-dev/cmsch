package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.*
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
) : ComponentBase("task", "/tasks", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            langGroup,
            profileRequiredTitle,
            profileRequiredMessage,
            regularTitle,
            regularMessage,

            exportGroup,
            exportEnabled
        )
    }

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
        fieldName = "Nyelvi bellítások",
        description = ""
    )

    val profileRequiredTitle = SettingProxy(componentSettingService, component,
        "profileRequiredTitle", "Kötelezően kitöltendő", type = SettingType.TEXT,
        fieldName = "Kötelező feladatok fejléc szövege", description = "Feladatok (PROFILE_REQUIRED) fejléc szövege"
    )

    val profileRequiredMessage = SettingProxy(componentSettingService, component,
        "profileRequiredMessage", "", type = SettingType.LONG_TEXT,
        fieldName = "Kötelező feladatok alatti szöveg",
        description = "Kötelező feladatok (PROFILE_REQUIRED) fejléce alatt megjelenő szöveg. Ha üres, akkor nincs."
    )

    val regularTitle = SettingProxy(componentSettingService, component,
        "regularTitle", "Feladatok", type = SettingType.TEXT,
        fieldName = "Feladatok fejléc szövege", description = "Feladatok (REGULAR) fejléc szövege"
    )

    val regularMessage = SettingProxy(componentSettingService, component,
        "regularMessage", "", type = SettingType.LONG_TEXT,
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
        fieldName = "Endpoint elérhető"
    )

}
