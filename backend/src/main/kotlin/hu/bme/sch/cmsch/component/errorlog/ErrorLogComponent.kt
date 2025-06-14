package hu.bme.sch.cmsch.component.errorlog

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.BooleanSettingRef
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.StringSettingRef
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
    componentSettingService,
    "errorlog",
    "/errorlog",
    "Kliens hibaüzenetek",
    ControlPermissions.PERMISSION_CONTROL_ERROR_LOG,
    listOf(ErrorLogEntity::class),
    env
) {

    val errorLogGroup by SettingGroup(fieldName = "Kliens hibák")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,  minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleok küldhetnek hibajelentéseket"
    )

    var receiveReports by BooleanSettingRef(true, fieldName = "Kliens hibajelentések fogadása", serverSideOnly = true
    )

}
