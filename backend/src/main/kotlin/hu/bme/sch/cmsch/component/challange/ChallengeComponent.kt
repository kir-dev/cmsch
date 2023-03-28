package hu.bme.sch.cmsch.component.challange

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.component.bmejegy.BmejegyRecordEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    ControlPermissions.PERMISSION_CONTROL_CHALLENGE,
    listOf(ChallengeSubmissionEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
