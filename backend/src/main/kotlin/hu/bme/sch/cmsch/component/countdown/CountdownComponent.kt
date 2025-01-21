package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["countdown"],
    havingValue = "true",
    matchIfMissing = false
)
class CountdownComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "countdown",
    "/",
    "Visszaszámlálás",
    ControlPermissions.PERMISSION_CONTROL_COUNTDOWN,
    listOf(),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            countdownGroup,
            title, minRole,

            enabled,
            showOnlyCountdownForRoles,
            keepOnAfterCountdownOver,
            topMessage,
            timeToCountTo,
            imageUrl,
            blurredImage,
        )
    }

    fun isBlockedAt(timeInSec: Long, role: RoleType): Boolean {
        if (!enabled.isValueTrue()) return false
        if (!showOnlyCountdownForRoles.isAvailableForRole(role)) return false
        val isCountdownOver = (timeToCountTo.getValue().toLongOrNull() ?: 0) < timeInSec
        return (keepOnAfterCountdownOver.isValueTrue() || !isCountdownOver)
    }

    val countdownGroup = SettingProxy(componentSettingService, component,
        "countdownGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Visszaszámlálás",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Hamarosan",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal",
        minRoleToEdit = RoleType.NOBODY
    )

    val enabled = SettingProxy(componentSettingService, component,
        "enabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Bekapcsolva", description = "Legyen aktív a visszaszámlálás komponens"
    )

    val showOnlyCountdownForRoles = MinRoleSettingProxy(componentSettingService, component,
        "showOnlyCountdownForRoles", MinRoleSettingProxy.ALL_ROLES,
        fieldName = "Kinek legyen erőltetett", description = "Ezek a roleok számára más komponensek ne legyenek elérhetőek. " +
                "Csak akkor működik, ha be van kapcsolva a komponens.",
        minRoleToEdit = RoleType.STAFF, grantedForRoles = ArrayList() // Thymeleaf doesn't like emptyList() (EmptyList)
    )

    val keepOnAfterCountdownOver = SettingProxy(componentSettingService, component,
        "keepOnAfterCountdownOver", "false", type = SettingType.BOOLEAN,
        fieldName = "Ne engedjen be az oldalra lejárat után",
        description = "Ha be van kapcsolva és erőltetett a visszaszámláló a felhasználó, akkor a lejárta után sem enged az oldalhoz hozzáférni"
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "Az esemény kezdetéig hátralévő idő:",
        type = SettingType.TEXT,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen. A [[ és ]] jelek között írt szöveg brand színű lesz."
    )

    val timeToCountTo = SettingProxy(componentSettingService, component,
        "timeToCountTo", "0", type = SettingType.DATE_TIME,
        fieldName = "Visszaszámlálás eddig"
    )

    val imageUrl = SettingProxy(componentSettingService, component,
        "imageUrl", "https://warp.sch.bme.hu/kir-dev/cmsch/countdown-bg.png", type = SettingType.URL,
        fieldName = "Háttérkép URL-je", description = ""
    )

    val blurredImage = SettingProxy(componentSettingService, component,
        "blurredImage", "true", type = SettingType.BOOLEAN,
        fieldName = "Elmosott háttér", description = "A háttérkép legyen elmosva (gaussian blur)"
    )

}
