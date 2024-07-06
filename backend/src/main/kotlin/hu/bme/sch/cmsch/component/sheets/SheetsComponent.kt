package hu.bme.sch.cmsch.component.sheets

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.component.SettingProxy
import hu.bme.sch.cmsch.component.SettingType
import hu.bme.sch.cmsch.component.app.ComponentSettingService
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    listOf(EventEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            sheetsGroup,
            minRole,
        )
    }

    val sheetsGroup = SettingProxy(componentSettingService, component,
        "sheetsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Sheets Integráció",
        description = ""
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}