package hu.bme.sch.cmsch.component.event

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["event"],
    havingValue = "true",
    matchIfMissing = false
)
class EventComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "event",
    "/event",
    "Események",
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    listOf(EventEntity::class),
    env
) {
    val eventsGroup by SettingGroup(fieldName = "Események")

    final var title by StringSettingRef("Programok", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override var menuDisplayName by StringSettingRef("Programok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override var minRole by MinRoleSettingRef(setOf(), fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup by SettingGroup(fieldName = "Megjelenés")

    var seekToCurrentCurrent by BooleanSettingRef(false, fieldName = "Tekerjen oda a jelenlegi programhoz"
    )

    var separateDays by BooleanSettingRef(false, fieldName = "Külön csoportosítva naponként"
    )

    var topMessage by StringSettingRef("Rövid szöveg a programokról általánosságban", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    var searchEnabled by BooleanSettingRef(false, fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup by SettingGroup(fieldName = "Működés")

    var enableDetailedView by BooleanSettingRef(false, fieldName = "Elérhető a részletes nézet (külön lapon)"
    )

    var filterByCategory by BooleanSettingRef(false, fieldName = "Ha be van kapcsolva, akkor lehet kategória alapján (is) lehet szűrni"
    )

    var filterByLocation by BooleanSettingRef(false, fieldName = "Ha be van kapcsolva, akkor lehet helyszín alapján (is) lehet szűrni"
    )

    var filterByDay by BooleanSettingRef(false, fieldName = "Ha be van kapcsolva, akkor lehet nap alapján (is) lehet szűrni"
    )

}
