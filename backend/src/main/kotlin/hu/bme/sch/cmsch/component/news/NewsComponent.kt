package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
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
) : ComponentBase(
    "news",
    "/news",
    "Hírek",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(NewsEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            newsGroup,
            title, menuDisplayName, minRole,
            showDetails,
        )
    }

    val newsGroup = SettingProxy(componentSettingService, component,
        "newsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Hírek",
        description = ""
    )

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

}
