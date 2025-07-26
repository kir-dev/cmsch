package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service


@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["tournament"],
    havingValue = "true",
    matchIfMissing = false
)
class TournamentComponent (
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "tournament",
    "/tournament",
    "Tournament",
    ControlPermissions.PERMISSION_CONTROL_TOURNAMENT,
    listOf(),
    env
){

    val tournamentGroup by SettingGroup(fieldName = "Versenyek")
    final var title by StringSettingRef("Sportversenyek",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")
    final override var menuDisplayName by StringSettingRef(
        "Sportversenyek", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )
    final override var minRole by MinRoleSettingRef(
        setOf(), fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal"
    )

    var showTournamentsAtAll by BooleanSettingRef(
        false, fieldName = "Leküldött",
        description = "Ha igaz, akkor leküldésre kerülnek a versenyek"
    )
}