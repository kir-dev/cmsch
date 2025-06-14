package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["service-account"],
    havingValue = "true",
    matchIfMissing = false
)
class ServiceAccountComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "service-account",
    "/",
    "Service Account",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ServiceAccountKeyEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    /// -------------------------------------------------------------------------------------------------------------------

    final override val minRole = MinRoleSettingRef(
        componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )
}
