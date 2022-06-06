package hu.bme.sch.cmsch.component.signup

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["signup"],
    havingValue = "true",
    matchIfMissing = false
)
class SignupComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("signup", "/form", componentSettingService, env) {


    final override val allSettings by lazy {
        listOf(
            minRole,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )


    // A jelentkezés még nem tölthető ki
    // A jelentkezés már nem tölthető ki
    // Nincs több férőhely
    // A jelentkezésed vissza lett utasítva
    // Jelentkezés elfogadva


}
