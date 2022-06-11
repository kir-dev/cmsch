package hu.bme.sch.cmsch.component.countdown

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
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
) : ComponentBase("countdown", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, minRole,

            enabled,
            topMessage,
            timeToCountTo,
            informativeOnly,
            imageUrl,
            blurredImage,
        )
    }

    fun isBlockedAt(timeInSec: Long): Boolean {
        return enabled.isValueTrue() && (informativeOnly.isValueTrue() || ((timeToCountTo.getValue().toLongOrNull() ?: 0) > timeInSec))
    }

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
        fieldName = "Bekapcsolva", description = "Legyen aktív a visszaszélálás komponens"
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "Az esemény kezdetéig hátralévő idő:",
        type = SettingType.TEXT,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val timeToCountTo = SettingProxy(componentSettingService, component,
        "timeToCountTo", "0", type = SettingType.DATE_TIME,
        fieldName = "Visszaszálálás eddig"
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
