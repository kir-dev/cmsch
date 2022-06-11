package hu.bme.sch.cmsch.component.extrapage

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["extraPage"],
    havingValue = "true",
    matchIfMissing = false
)
class ExtraPageComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("extraPage", "/page", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
