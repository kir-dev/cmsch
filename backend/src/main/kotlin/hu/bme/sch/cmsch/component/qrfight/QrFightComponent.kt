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
    componentSettingService,
    "qrFight",
    "/qr-fight",
    "QR Fight",
    ControlPermissions.PERMISSION_CONTROL_QR_FIGHT,
    listOf(QrLevelEntity::class, QrTowerEntity::class),
    env
) {

    val qrFightGroup by SettingGroup(fieldName = "QR Fight")

    final var title by StringSettingRef("QR Fight",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("QR Fight", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.SUPERUSER,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    var enabled by BooleanSettingRef(false, fieldName = "QR Fight engedélyezve",
        description = "Ha be van kapcsolva, akkor mennek a QR fightos endpointok"
    )

    var topMessage by StringSettingRef("",
        type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    var apiTokens by StringSettingRef("tower:token", serverSideOnly = true, fieldName = "API tokenek",
        description = "Formátum: towerSelector:token, ..."
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val indulaschGroup by SettingGroup(fieldName = "Indulásch integráció")

    var indulaschTowerEnabled by BooleanSettingRef(false, serverSideOnly = true, fieldName = "Indulásch torony",
        description = "Ha be van kapcsolva, akkor az indulásch apin állítja a tábla szövegét"
    )

    var indulaschTowerSelector by StringSettingRef("indulasch", serverSideOnly = true, fieldName = "Torony selector",
        description = "Melyik torony legyen az InduláSch torony?"
    )

    var indulaschKioskId by StringSettingRef("", serverSideOnly = true, fieldName = "Kioszk azonosító",
        description = "Ezt a kioszkot fogja szerkeszteni a szerver", minRoleToEdit = RoleType.SUPERUSER
    )

    var indulaschApiKey by StringSettingRef("", serverSideOnly = true, fieldName = "API Kulcs",
        description = "API kulcs az InduláSch-hoz.", minRoleToEdit = RoleType.SUPERUSER
    )


}
