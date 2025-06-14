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
    "messaging",
    "",
    "Értesítések",
    ControlPermissions.PERMISSION_CONTROL_MESSAGING,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            settingGroup,
            minRole,

            proxyBaseUrl,
            proxyToken,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val settingGroup = ControlGroup(component, "settingGroup", fieldName = "Proxy adatai")

    val proxyBaseUrl = StringSettingRef(componentSettingService, component,
        "proxyBaseUrl", "http://localhost:8080", type = SettingType.URL,
        fieldName = "A proxy elérhetősége", serverSideOnly = true
    )

    val proxyToken = StringSettingRef(componentSettingService, component,
        "proxyToken", "", type = SettingType.TEXT, fieldName = "Token a proxyhoz", serverSideOnly = true
    )

}
