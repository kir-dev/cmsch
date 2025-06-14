package hu.bme.sch.cmsch.component.challenge

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
    name = ["challenge"],
    havingValue = "true",
    matchIfMissing = false
)
class ChallengeComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "challenge",
    "/",
    "Beadások",
    ImplicitPermissions.PERMISSION_NOBODY,
    listOf(ChallengeSubmissionEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            challengeGroup,
            minRole,
        )
    }

    val challengeGroup = SettingGroup(component, "challengeGroup", fieldName = "Beadások")

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
