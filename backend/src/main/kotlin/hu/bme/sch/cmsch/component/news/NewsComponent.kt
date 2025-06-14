package hu.bme.sch.cmsch.component.news

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
) {

    final override val allSettings by lazy {
        listOf(
            newsGroup,
            title, menuDisplayName, minRole,
            showDetails,
        )
    }

    val newsGroup = SettingGroup(component, "newsGroup", fieldName = "Hírek")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Hírek",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Hírek", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val showDetails = BooleanSettingRef(componentSettingService,
        component, "showDetails", false, fieldName = "Részletes nézet",
        description = "Ha be van kapcsolva akkor a elérhetőek a cikkek külön lapon is"
    )

}
