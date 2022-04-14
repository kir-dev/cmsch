package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["debt"],
    havingValue = "true",
    matchIfMissing = false
)
class DebtComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("debt", "/debt", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )
}
