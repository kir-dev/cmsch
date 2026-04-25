package hu.bme.sch.cmsch.component.boat

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.boat"])
class BoatComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "boat",
    "/boat",
    "Vitorlás Kupa",
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    entities = listOf(SignupEntity::class),
    env = env
) {
    val boatRaceGroup by SettingGroup(fieldName = "Vitorlás Kupa")

    final var title by StringSettingRef("Vitorlás Kupa",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Vitorlás Kupa", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(),
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

}