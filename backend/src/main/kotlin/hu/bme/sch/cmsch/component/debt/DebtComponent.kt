package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
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
) : ComponentBase(
    "debt",
    "/debt",
    "Tartozások",
    ControlPermissions.PERMISSION_CONTROL_DEBTS,
    listOf(ProductEntity::class, SoldProductEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            debtGroup,
            title,
            menuDisplayName,
            minRole,
            topMessage,
        )
    }

    val debtGroup = SettingProxy(componentSettingService, component,
        "debtGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tartozások",
        description = "Jelenleg nincs mit beállítani itt"
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Fogyasztás",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Fogyasztás", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

}
