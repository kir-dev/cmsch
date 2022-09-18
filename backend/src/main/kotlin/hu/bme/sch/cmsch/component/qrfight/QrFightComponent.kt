package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["qrFight"],
    havingValue = "true",
    matchIfMissing = false
)
class QrFightComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("qrFight", "/qr-fight", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            title, menuDisplayName, minRole,

            enabled,
            topMessage,
            apiTokens,

            indulaschGroup,
            indulaschTowerEnabled,
            indulaschMessageFormat,
            indulaschMessagePrefix,
            indulaschMessageLevel,
            indulaschTowerSelector
        )
    }

    final val title = SettingProxy(componentSettingService, component,
        "title", "QR Fight",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "QR Fight", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val enabled = SettingProxy(componentSettingService, component,
        "enabled", "false", type = SettingType.BOOLEAN,
        fieldName = "QR Fight engedélyezve", description = "Ha be van kapcsolva, akkor mennek a QR fightos endpointok"
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val apiTokens = SettingProxy(componentSettingService, component,
        "apiTokens", "tower:token", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "API tokenek", description = "Formátum: towerSelector:token, ..."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val indulaschGroup = SettingProxy(componentSettingService, component,
        "indulaschGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Indulásch integráció"
    )

    val indulaschTowerEnabled = SettingProxy(componentSettingService, component,
        "indulaschTowerEnabled", "false", type = SettingType.BOOLEAN, serverSideOnly = true,
        fieldName = "Indulásch torony", description = "Ha be van kapcsolva, akkor az indulásch apin állítja a tábla szövegét"
    )

    val indulaschMessageFormat = SettingProxy(componentSettingService, component,
        "indulaschMessageFormat", "[QR FIGHT] AZ INDULÁSCH TORNYOT JELELEG A(Z) >> {OWNER} << FOGLALJA, ÉS {TIME} PERCCEL A(Z) >> {HOLDER} << BIRTOKOLJA!",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Üzenet formátum", description = "Változók: {OWNER} {TIME} {HOLDER}"
    )

    val indulaschMessagePrefix = SettingProxy(componentSettingService, component,
        "indulaschMessagePrefix", "[QR FIGHT] ",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Üzenet prefix", description = "Ez alapján törli ki az api a többi üzenetet"
    )

    val indulaschMessageLevel = SettingProxy(componentSettingService, component,
        "indulaschMessageLevel", "FUN",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Üzenet szintje", description = "INFO | WARNING | SUCCESS | FUN"
    )

    val indulaschTowerSelector = SettingProxy(componentSettingService, component,
        "indulaschTowerSelector", "indulasch",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Torony selector", description = ""
    )

}
