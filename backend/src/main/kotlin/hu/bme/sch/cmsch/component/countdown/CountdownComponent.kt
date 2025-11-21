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
        description = "Melyik roleokkal nyitható meg az oldal", minRoleToEdit = RoleType.NOBODY)

    var enabled by BooleanSettingRef(false, fieldName = "Bekapcsolva",
        description = "Legyen aktív a visszaszámlálás komponens")

    var showOnlyCountdownForRoles by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES,
        fieldName = "Kinek legyen erőltetett",
        description = "Ezek a roleok számára más komponensek ne legyenek elérhetőek. Csak akkor működik, ha be van kapcsolva a komponens.",
        minRoleToEdit = RoleType.STAFF, grantedForRoles = setOf())

    var keepOnAfterCountdownOver by BooleanSettingRef(false, fieldName = "Ne engedjen be az oldalra lejárat után",
        description = "Ha be van kapcsolva és erőltetett a visszaszámláló a felhasználó, akkor a lejárta után sem enged az oldalhoz hozzáférni")

    var topMessage by StringSettingRef("Az esemény kezdetéig hátralévő idő:",
        fieldName = "Oldal tetején megjelenő szöveg",
        description = "Ha üres akkor nincs ilyen. A [[ és ]] jelek között írt szöveg brand színű lesz.")

    var showRemainingTime by BooleanSettingRef(defaultValue = true, fieldName = "Hátralévő idő mutatása")

    var timeToCountTo by NumberSettingRef(type = SettingType.DATE_TIME,
        fieldName = "Visszaszámlálás eddig", strictConversion = false)

    var imageUrl by StringSettingRef("", type = SettingType.IMAGE_URL, fieldName = "Háttérkép URL-je")

    var blurredImage by BooleanSettingRef(true, fieldName = "Elmosott háttér",
        description = "A háttérkép legyen elmosva (gaussian blur)")

}
