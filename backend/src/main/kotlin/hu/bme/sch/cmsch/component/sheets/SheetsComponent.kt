package hu.bme.sch.cmsch.component.sheets

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
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
    componentSettingService,
    "sheets",
    "/",
    "Sheets (beta)",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(EventEntity::class),
    env
) {

    val sheetsGroup by SettingGroup(fieldName = "Sheets Integráció")

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
