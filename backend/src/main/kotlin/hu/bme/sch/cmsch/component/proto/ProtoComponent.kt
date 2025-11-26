package hu.bme.sch.cmsch.component.proto

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ImplicitPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.proto"])
class ProtoComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "proto",
    "/proto",
    "Prototípusok",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ProtoEntity::class),
    env
) {

    val protoGroup by SettingGroup(fieldName = "Prototípusok")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")


}
