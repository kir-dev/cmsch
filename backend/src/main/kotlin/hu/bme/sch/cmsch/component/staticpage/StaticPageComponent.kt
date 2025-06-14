package hu.bme.sch.cmsch.component.staticpage

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.ControlGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingRef
import hu.bme.sch.cmsch.setting.SettingType
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
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(StaticPageEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            staticPageGroup,
            minRole
        )
    }

    val staticPageGroup = ControlGroup( component, "staticPageGroup", fieldName = "Statikus Oldalak",
        description = "Jelenleg nincs mit beállítani itt"
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
