package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
) : ComponentBase(
    "qrFight",
    "/qr-fight",
    "QR Fight",
    ControlPermissions.PERMISSION_CONTROL_QR_FIGHT,
    listOf(QrLevelEntity::class, QrTowerEntity::class),
    componentSettingService, env
) {

    final override val allSettings by lazy {
        listOf(
            qrFightGroup,
            title, menuDisplayName, minRole,

            enabled,
            topMessage,
            apiTokens,

            indulaschGroup,
            indulaschTowerEnabled,
            indulaschTowerSelector,
            indulaschKioskId,
            indulaschApiKey,
        )
    }

    val qrFightGroup = SettingProxy(componentSettingService, component,
        "qrFightGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "QR Fight",
        description = ""
    )

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

    val indulaschTowerSelector = SettingProxy(componentSettingService, component,
        "indulaschTowerSelector", "indulasch",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Torony selector", description = "Melyik torony legyen az InduláSch torony?"
    )

    val indulaschKioskId = SettingProxy(componentSettingService, component,
        "indulaschKioskId", "",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Kioszk azonosító", description = "Ezt a kioszkot fogja szerkeszteni a szerver", minRoleToEdit = RoleType.SUPERUSER
    )

    val indulaschApiKey = SettingProxy(componentSettingService, component,
        "indulaschApiKey", "",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "API Kulcs", description = "API kulcs az InduláSch-hoz.", minRoleToEdit = RoleType.SUPERUSER
    )


}
