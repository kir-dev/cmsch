package hu.bme.sch.cmsch.component.tournament

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.setting.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    "tournament",
    "/tournament",
    "Tournament",
    ControlPermissions.PERMISSION_CONTROL_TOURNAMENT,
    listOf(),
    env
){
    final override val allSettings by lazy {
        listOf(
            tournamentGroup,
            minRole,
        )
    }

    final override val menuDisplayName = SettingProxy(
        componentSettingService, component,
        "menuDisplayName", "Sportversenyek", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final val tournamentGroup = SettingProxy(componentSettingService, component,
        "tournamentGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tournament",
        description = "Jelenleg nincs mit beállítani itt"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.ADMIN,
        fieldName = "Minimum jogosultság", description = "A komponens eléréséhez szükséges minimum jogosultság"
    )

    val showTournamentsAtAll = SettingProxy(componentSettingService, component,
        "showTournamentsAtAll", "true", type = SettingType.BOOLEAN, serverSideOnly =  true,
        fieldName = "Versenylista megjelenítése", description = "Ha ki van kapcsolva, akkor a versenylista nincsen leküldve",
    )
}