package hu.bme.sch.cmsch.component.proto

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(ProtoEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            protoGroup,
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val protoGroup = SettingProxy(componentSettingService, component,
        "protoGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Prototípusok",
        description = ""
    )

}