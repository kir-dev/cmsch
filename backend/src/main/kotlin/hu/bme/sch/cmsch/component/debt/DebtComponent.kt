package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingType
import hu.bme.sch.cmsch.setting.StringSettingRef
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
    componentSettingService,
    "debt",
    "/debt",
    "Tartozások",
    ControlPermissions.PERMISSION_CONTROL_DEBTS,
    listOf(ProductEntity::class, SoldProductEntity::class),
    env
) {

    val debtGroup by SettingGroup(fieldName = "Tartozások", description = "Jelenleg nincs mit beállítani itt")

    final var title by StringSettingRef("Fogyasztás", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Fogyasztás", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    var topMessage by StringSettingRef("", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

}
