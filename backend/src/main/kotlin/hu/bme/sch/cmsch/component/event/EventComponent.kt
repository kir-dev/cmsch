package hu.bme.sch.cmsch.component.event

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

    val eventsGroup = SettingProxy(componentSettingService, component,
        "eventsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Események",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Programok",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Programok", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appearanceGroup = SettingProxy(componentSettingService, component,
        "appearanceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Megjelenés",
        description = ""
    )

    val seekToCurrentCurrent = SettingProxy(componentSettingService, component,
        "seekToCurrentCurrent", "false", type = SettingType.BOOLEAN,
        fieldName = "Tekerjen oda a jelenlegi programhoz"
    )

    val separateDays = SettingProxy(componentSettingService, component,
        "separateDays", "false", type = SettingType.BOOLEAN,
        fieldName = "Külön csoportosítva naponként"
    )

    val topMessage = SettingProxy(componentSettingService, component,
        "topMessage", "Rövid szöveg a programokról általánosságban", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
    )

    val searchEnabled = SettingProxy(componentSettingService, component,
        "searchEnabled", "false", type = SettingType.BOOLEAN,
        fieldName = "Keresés elérhető",
        description = "Legyen-e kereső az oldal tetején"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val logicGroup = SettingProxy(componentSettingService, component,
        "logicGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Működés",
        description = ""
    )

    val enableDetailedView = SettingProxy(componentSettingService, component,
        "enableDetailedView", "false", type = SettingType.BOOLEAN,
        fieldName = "Elérhető a részletes nézet (külön lapon)"
    )

    val filterByCategory = SettingProxy(componentSettingService, component,
        "filterByCategory", "false", type = SettingType.BOOLEAN,
        fieldName = "Ha be van kapcsolva, akkor lehet kategória alapján (is) lehet szűrni"
    )

    val filterByLocation = SettingProxy(componentSettingService, component,
        "filterByLocation", "false", type = SettingType.BOOLEAN,
        fieldName = "Ha be van kapcsolva, akkor lehet helyszín alapján (is) lehet szűrni"
    )

    val filterByDay = SettingProxy(componentSettingService, component,
        "filterByDay", "false", type = SettingType.BOOLEAN,
        fieldName = "Ha be van kapcsolva, akkor lehet nap alapján (is) lehet szűrni"
    )

}
