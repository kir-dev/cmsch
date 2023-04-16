package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["debt"],
    havingValue = "true",
    matchIfMissing = false
)
class DebtComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "debt",
    "/debt",
    "Tartozások",
    ControlPermissions.PERMISSION_CONTROL_DEBTS,
    listOf(ProductEntity::class, SoldProductEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            debtGroup,
            minRole
        )
    }

    val debtGroup = SettingProxy(componentSettingService, component,
        "debtGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tartozások",
        description = "Jelenleg nincs mit beállítani itt"
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )
}
