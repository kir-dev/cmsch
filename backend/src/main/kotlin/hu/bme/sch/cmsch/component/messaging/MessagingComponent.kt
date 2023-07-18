package hu.bme.sch.cmsch.component.messaging

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val settingGroup = SettingProxy(componentSettingService, component,
        "settingGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Proxy adatai", serverSideOnly = true,
        description = ""
    )

    val proxyBaseUrl = SettingProxy(componentSettingService, component,
        "proxyBaseUrl", "http://127.0.0.1:8080", type = SettingType.COLOR,
        fieldName = "A proxy elérhetősége", serverSideOnly = true
    )

    val proxyToken = SettingProxy(componentSettingService, component,
        "proxyToken", "", type = SettingType.TEXT,
        fieldName = "Token a proxyhoz", serverSideOnly = true
    )

}