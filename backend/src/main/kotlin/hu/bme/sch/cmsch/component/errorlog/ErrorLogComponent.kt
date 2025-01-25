package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["errorlog"],
    havingValue = "true",
    matchIfMissing = false
)
class ErrorLogComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "errorlog",
    "/errorlog",
    "Kliens hibaüzenetek",
    ControlPermissions.PERMISSION_CONTROL_ERROR_LOG,
    listOf(ErrorLogEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(errorLogGroup, menuDisplayName, minRole, receiveReports)
    }

    val errorLogGroup = SettingProxy(componentSettingService, component,
        "errorLogGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kliens hibák",
        description = ""
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Kliens hibák", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,  minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleok küldhetnek hibajelentéseket"
    )

    val receiveReports = SettingProxy(componentSettingService, component, "receiveReports", "true",
        type = SettingType.BOOLEAN, fieldName = "Kliens hibajelentések fogadása", serverSideOnly = true
    )

}
