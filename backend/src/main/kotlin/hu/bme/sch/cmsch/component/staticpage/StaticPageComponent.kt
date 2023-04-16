package hu.bme.sch.cmsch.component.staticpage

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["staticPage"],
    havingValue = "true",
    matchIfMissing = false
)
class StaticPageComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "staticPage",
    "/page",
    "Statikus Oldalak",
    ControlPermissions.PERMISSION_CONTROL_STATIC_PAGES,
    listOf(StaticPageEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            staticPageGroup,
            minRole
        )
    }

    val staticPageGroup = SettingProxy(componentSettingService, component,
        "staticPageGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Statikus Oldalak",
        description = "Jelenleg nincs mit beállítani itt"
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
