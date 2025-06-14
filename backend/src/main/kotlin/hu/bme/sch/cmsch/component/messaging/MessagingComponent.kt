package hu.bme.sch.cmsch.component.messaging

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["messaging"],
    havingValue = "true",
    matchIfMissing = false
)
class MessagingComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "messaging",
    "",
    "Értesítések",
    ControlPermissions.PERMISSION_CONTROL_MESSAGING,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val settingGroup by SettingGroup(fieldName = "Proxy adatai")

    var proxyBaseUrl by StringSettingRef("http://localhost:8080", type = SettingType.URL,
        fieldName = "A proxy elérhetősége", serverSideOnly = true
    )

    var proxyToken by StringSettingRef("", fieldName = "Token a proxyhoz", serverSideOnly = true
    )

}
