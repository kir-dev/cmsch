package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["groupselection"],
    havingValue = "true",
    matchIfMissing = false
)
class GroupSelectionComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "groupselection",
    "/",
    "Csapat választás",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", RoleType.BASIC.name, minRoleToEdit = RoleType.STAFF,
        fieldName = "Jogosultságok", description = "Melyik roleokkal lehetséges a csoport választás"
    )

}
