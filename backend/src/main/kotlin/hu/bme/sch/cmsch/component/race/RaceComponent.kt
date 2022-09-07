package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["race"],
    havingValue = "true",
    matchIfMissing = false
)
class RaceComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("race", "/race", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            displayGroup,
            visible,
            ascendingOrder
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Sörmérés",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Sörmérés", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val displayGroup = SettingProxy(componentSettingService, component,
        "displayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kijelzés",
        description = ""
    )

    val visible = SettingProxy(componentSettingService, component,
        "visible", "false", type = SettingType.BOOLEAN,
        fieldName = "Látható",
        description = "Leküldésre kerüljön-e a toplista"
    )

    val ascendingOrder = SettingProxy(componentSettingService, component,
        "ascendingOrder", "true", type = SettingType.BOOLEAN,
        fieldName = "Növekvő sorrend",
        description = "A toplista elemei növekvő sorrendben vannak"
    )

}
