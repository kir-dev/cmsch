package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class DebugComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "debug",
    "/",
    "Fejlesztő beállítások",
    ControlPermissions.PERMISSION_DEV_DEBUG,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            debugGroup,
            submitDiff
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val debugGroup = SettingProxy(componentSettingService, component,
        "debugGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "DEBUG", description = "Ehhez csak akkor nyúlj, ha tudod mit csinálsz!"
    )

    val submitDiff = SettingProxy(componentSettingService, component,
        "submitDiff", "-7200", type = SettingType.NUMBER,
        fieldName = "Task beadás diff", serverSideOnly = true
    )

}