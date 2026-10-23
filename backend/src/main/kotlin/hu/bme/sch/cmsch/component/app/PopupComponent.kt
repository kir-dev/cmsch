package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.BooleanSettingRef
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.NumberSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.SettingType
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Conditional
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(ApplicationComponent::class)
class PopupComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "popup",
    "/",
    "Popup",
    ControlPermissions.PERMISSION_CONTROL_APP,
    listOf(),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val jumpscareGroup by SettingGroup(fieldName = "Jumpscare beállítások")

    val popupEnabled by BooleanSettingRef(false, fieldName = "Felugró média engedélyezése", description = "Engedélyezi a felugró média megjelenítését az oldalon")

    val popupUrls by StringSettingRef(
        fieldName = "Felugró média", type = SettingType.LONG_TEXT,
        description = "A felugró médiák elérési útjai, vesszővel elválasztva"
    )

    val popupBackground by StringSettingRef(
        defaultValue = "#000000", type = SettingType.COLOR,
        fieldName = "Felugró média háttérszíne",
        description = "A felugró média háttérszíne hexadecimális formátumban (pl. #000000)"
    )

    val popupOpacity by NumberSettingRef(
        defaultValue = 80,
        fieldName = "Felugró média átlátszósága",
        description = "A felugró média átlátszósága 0 és 100 között (0 = teljesen átlátszó, 100 = teljesen átlátszatlan)"
    )

    val intervalLowBound by NumberSettingRef(
        defaultValue = 60,
        fieldName = "Felugró média alsó intervallum",
        description = "Legutóbbi megjelenésétől számítva legalább ennyi másodpercnek kell eltelnie a következő felugró média megjelenéséig",
    )

    val intervalHighBound by NumberSettingRef(
        defaultValue = 60,
        fieldName = "Felugró média felső intervallum",
        description = "Legutóbbi megjelenésétől számítva legfeljebb ennyi másodpercnek kell eltelnie a következő felugró média megjelenéséig"
    )
}