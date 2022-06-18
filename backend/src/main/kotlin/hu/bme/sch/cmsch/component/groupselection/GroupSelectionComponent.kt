package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("groupselection", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            selectionEnabled,
            minRole,
        )
    }

    final override val menuDisplayName = null

    val selectionEnabled = SettingProxy(componentSettingService, component,
        "selectionEnabled", "true", type = SettingType.BOOLEAN,
        fieldName = "Választás engedélyezve", description = "Csak akkor jelenik meg a lehetőség ha ez be van kapcsolva"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", RoleType.BASIC.name, minRoleToEdit = RoleType.STAFF,
        fieldName = "Jogosultságok", description = "Melyik roleokkal lehetséges a csoport választás"
    )

}
