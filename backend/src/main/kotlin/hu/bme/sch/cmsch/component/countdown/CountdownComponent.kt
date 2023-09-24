package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
            showOnly,
            topMessage,
            timeToCountTo,
            informativeOnly,
            imageUrl,
            blurredImage,
        )
    }

    fun isBlockedAt(timeInSec: Long): Boolean {
        return enabled.isValueTrue() && showOnly.isValueTrue() && (informativeOnly.isValueTrue() || ((timeToCountTo.getValue().toLongOrNull() ?: 0) > timeInSec))
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

    val showOnly = SettingProxy(componentSettingService, component,
        "showOnly", "true", type = SettingType.BOOLEAN,
        fieldName = "Erőltetett", description = "Más komponensek ne legyenek elérhetőek. " +
                "Csak akkor működik, ha be van kapcsolva  a komponens."
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

    val informativeOnly = SettingProxy(componentSettingService, component,
        "informativeOnly", "false", type = SettingType.BOOLEAN,
        fieldName = "Tájékoztató jellegű",
        description = "Ha be van kapcsolva akkor a lejárta után sem enged az oldalhoz hozzáférni"
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
