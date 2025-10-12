package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.service-account"])
class ServiceAccountComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "service-account",
    "/",
    "Service Account",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ServiceAccountKeyEntity::class),
    env
) {

    /// -------------------------------------------------------------------------------------------------------------------

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")
}
