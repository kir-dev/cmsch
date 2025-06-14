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
    "event",
    "/event",
    "Események",
    ControlPermissions.PERMISSION_CONTROL_EVENTS,
    listOf(EventEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            eventsGroup,
            title, menuDisplayName, minRole,

            appearanceGroup,
            seekToCurrentCurrent,
            separateDays,
            topMessage,
            searchEnabled,

            logicGroup,
            enableDetailedView,
            filterByCategory,
            filterByLocation,
            filterByDay
        )
    }

    val eventsGroup = ControlGroup(component, "eventsGroup", fieldName = "Események")

    final val title = StringSettingRef(componentSettingService, component,
        "title", "Programok", fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = StringSettingRef(componentSettingService, component,
        "menuDisplayName", "Programok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = ControlGroup(component, "appearanceGroup", fieldName = "Megjelenés")

    val seekToCurrentCurrent = BooleanSettingRef(componentSettingService, component,
        "seekToCurrentCurrent", false, fieldName = "Tekerjen oda a jelenlegi programhoz"
    )

    val separateDays = BooleanSettingRef(componentSettingService, component,
        "separateDays", false, fieldName = "Külön csoportosítva naponként"
    )

    val topMessage = StringSettingRef(componentSettingService, component,
        "topMessage", "Rövid szöveg a programokról általánosságban", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val searchEnabled = BooleanSettingRef(componentSettingService, component,
        "searchEnabled", false, fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = ControlGroup(component, "logicGroup", fieldName = "Működés")

    val enableDetailedView = BooleanSettingRef(componentSettingService, component,
        "enableDetailedView", false, fieldName = "Elérhető a részletes nézet (külön lapon)"
    )

    val filterByCategory = BooleanSettingRef(componentSettingService, component,
        "filterByCategory", false, fieldName = "Ha be van kapcsolva, akkor lehet kategória alapján (is) lehet szűrni"
    )

    val filterByLocation = BooleanSettingRef(componentSettingService, component,
        "filterByLocation", false, fieldName = "Ha be van kapcsolva, akkor lehet helyszín alapján (is) lehet szűrni"
    )

    val filterByDay = BooleanSettingRef(componentSettingService, component,
        "filterByDay", false, fieldName = "Ha be van kapcsolva, akkor lehet nap alapján (is) lehet szűrni"
    )

}
