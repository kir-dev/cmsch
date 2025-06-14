package hu.bme.sch.cmsch.component.sheets

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service


@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["sheets"],
    havingValue = "true",
    matchIfMissing = false
)
class SheetsComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "sheets",
    "/",
    "Sheets (beta)",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(EventEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            sheetsGroup,
            minRole,
        )
    }

    val sheetsGroup = SettingGroup(component, "sheetsGroup", fieldName = "Sheets Integráció")

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
