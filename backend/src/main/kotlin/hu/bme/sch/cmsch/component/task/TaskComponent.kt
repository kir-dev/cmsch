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

            exportGroup,
            exportEnabled
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "BucketList",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "BucketList", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
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
