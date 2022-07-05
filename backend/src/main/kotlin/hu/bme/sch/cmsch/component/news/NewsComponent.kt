package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["news"],
    havingValue = "true",
    matchIfMissing = false
)
class NewsComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("news", "/news", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,
            showDetails,

            newsEmbeddedComponentGroup,
            maxVisibleCount
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "Hírek",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Hírek", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val showDetails = SettingProxy(componentSettingService, component,
        "showDetails", "false", type = SettingType.BOOLEAN,
        fieldName = "Részletes nézet",
        description = "Ha be van kapcsolva akkor a elérhetőek a cikkek külön lapon is"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val newsEmbeddedComponentGroup = SettingProxy(componentSettingService, component,
        "embeddedGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Beágyazott hírek komponensben megjelenő hírek száma",
        description = ""
    )

    val maxVisibleCount = SettingProxy(componentSettingService, component,
        "embeddedMaxVisibleCount", "3", serverSideOnly = true, type = SettingType.NUMBER,
        fieldName = "Max megjelenő hír", description = "Ennyi hír jelenik meg a főoldali hírdetés komponensben"
    )

}
