package hu.bme.sch.cmsch.component.groupselection

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
    name = ["groupselection"],
    havingValue = "true",
    matchIfMissing = false
)
class GroupSelectionComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "groupselection",
    "/",
    "Csapat választás",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(setOf(RoleType.BASIC), minRoleToEdit = RoleType.STAFF,
        fieldName = "Jogosultságok", description = "Melyik roleokkal lehetséges a csoport választás")

}
