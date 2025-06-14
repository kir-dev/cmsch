package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
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
    env
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

    val qrFightGroup = ControlGroup(component, "qrFightGroup", fieldName = "QR Fight")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "QR Fight",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "QR Fight", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    val enabled = BooleanSettingRef(componentSettingService, component,
        "enabled", false, fieldName = "QR Fight engedélyezve",
        description = "Ha be van kapcsolva, akkor mennek a QR fightos endpointok"
    )

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val apiTokens = StringSettingRef(componentSettingService, component,
        "apiTokens", "tower:token", type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "API tokenek", description = "Formátum: towerSelector:token, ..."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val indulaschGroup = ControlGroup(component, "indulaschGroup", fieldName = "Indulásch integráció")

    val indulaschTowerEnabled = BooleanSettingRef(componentSettingService, component,
        "indulaschTowerEnabled", false, serverSideOnly = true, fieldName = "Indulásch torony",
        description = "Ha be van kapcsolva, akkor az indulásch apin állítja a tábla szövegét"
    )

    val indulaschTowerSelector = StringSettingRef(componentSettingService, component,
        "indulaschTowerSelector", "indulasch",
        type = SettingType.TEXT, serverSideOnly = true,
        fieldName = "Torony selector", description = "Melyik torony legyen az InduláSch torony?"
    )

    val indulaschKioskId = StringSettingRef(componentSettingService,
        component, "indulaschKioskId", "", serverSideOnly = true, fieldName = "Kioszk azonosító",
        description = "Ezt a kioszkot fogja szerkeszteni a szerver", minRoleToEdit = RoleType.SUPERUSER
    )

    val indulaschApiKey = StringSettingRef(componentSettingService, component,
        "indulaschApiKey", "", serverSideOnly = true, fieldName = "API Kulcs",
        description = "API kulcs az InduláSch-hoz.", minRoleToEdit = RoleType.SUPERUSER
    )


}
