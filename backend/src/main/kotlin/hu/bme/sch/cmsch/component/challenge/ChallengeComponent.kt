package hu.bme.sch.cmsch.component.challenge

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
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.challenge"])
class ChallengeComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "challenge",
    "/",
    "Beadások",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ChallengeSubmissionEntity::class),
    env
) {

    val challengeGroup by SettingGroup(fieldName = "Beadások")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(setOf(), minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

}
