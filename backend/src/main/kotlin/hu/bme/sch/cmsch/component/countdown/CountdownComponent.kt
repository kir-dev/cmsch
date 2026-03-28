package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.util.isAvailableForRole
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.countdown"])
class CountdownComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "countdown",
    "/",
    "Visszaszámlálás",
    ControlPermissions.PERMISSION_CONTROL_COUNTDOWN,
    listOf(),
    env
) {

    fun isBlockedAt(timeInSec: Long, role: RoleType): Boolean {
        if (!enabled) return false
        if (!showOnlyCountdownForRoles.isAvailableForRole(role)) return false
        val isCountdownOver = (timeToCountTo) < timeInSec
        return (keepOnAfterCountdownOver || !isCountdownOver)
    }

    val countdownGroup by SettingGroup(fieldName = "Visszaszámlálás")

    final var title by StringSettingRef("Hamarosan", fieldName = "Lap címe",
        description = "Ez jelenik meg a böngésző címsorában")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, fieldName = "Jogosultságok",
        description = "Mely szerepkörökkel nyitható meg az oldal", minRoleToEdit = RoleType.NOBODY)

    var enabled by BooleanSettingRef(false, fieldName = "Bekapcsolva",
        description = "Aktív legyen-e a visszaszámlálás")

    var showOnlyCountdownForRoles by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Kinek legyen kényszerített",
        description = "A megadott szerepkörök számára csak a visszaszámláló oldal legyen elérhető. Csak akkor működik, ha a komponens be van kapcsolva.",
        minRoleToEdit = RoleType.STAFF, grantedForRoles = setOf())

    var keepOnAfterCountdownOver by BooleanSettingRef(false, fieldName = "Ne engedjen be az oldalra lejárat után",
        description = "Ha a visszaszámláló kényszerített, a lejárta után sem engedi a hozzáférést a többi oldalhoz")

    var topMessage by StringSettingRef("Az esemény kezdetéig hátralévő idő:",
        fieldName = "Oldal tetején megjelenő szöveg",
        description = "Az oldal tetején megjelenő szöveg. A [[ és ]] jelek közötti rész kiemelt színű lesz. Ha üres, nem jelenik meg.")

    var showRemainingTime by BooleanSettingRef(defaultValue = true, fieldName = "Hátralévő idő mutatása")

    var timeToCountTo by NumberSettingRef(type = SettingType.DATE_TIME,
        fieldName = "Visszaszámlálás eddig", strictConversion = false)

    var imageUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Háttérkép URL-je")

    var blurredImage by BooleanSettingRef(true, fieldName = "Elmosott háttér",
        description = "A háttérkép legyen elmosva (gaussian blur)")

}
