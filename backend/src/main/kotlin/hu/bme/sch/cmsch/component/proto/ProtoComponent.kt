package hu.bme.sch.cmsch.component.proto

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["proto"],
    havingValue = "true",
    matchIfMissing = false
)
class ProtoComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "proto",
    "/proto",
    "Prototípusok",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ProtoEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            protoGroup,
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val protoGroup = SettingGroup(component, "protoGroup", fieldName = "Prototípusok")

}
