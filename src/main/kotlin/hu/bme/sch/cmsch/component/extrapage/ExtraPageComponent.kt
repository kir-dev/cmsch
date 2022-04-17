package hu.bme.sch.cmsch.component.extrapage

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["extraPage"],
    havingValue = "true",
    matchIfMissing = false
)
class ExtraPageComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("extraPage", "/page", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            appearanceGroup,
            seekToCurrentCurrent,
            separateDays,
            topMessage,

            logicGroup,
            enableDetailedView
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
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
        "topMessage", "Rövid szöveg a programokról általánosságban", type = SettingType.LONG_TEXT,
        fieldName = "Oldal tetején megjelenő szöveg", description = "Ha üres akkor nincs ilyen"
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
}
