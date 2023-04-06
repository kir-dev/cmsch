package hu.bme.sch.cmsch.component.staticpage

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
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
            minRole
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
