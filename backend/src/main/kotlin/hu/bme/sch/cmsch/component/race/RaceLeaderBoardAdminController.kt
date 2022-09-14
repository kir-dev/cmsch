package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RACE
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/race-leaderboard")
@ConditionalOnBean(RaceComponent::class)
class RaceLeaderBoardAdminController(
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    private val raceService: RaceService,
    private val startupPropertyConfig: StartupPropertyConfig
) : AbstractAdminPanelController<RaceEntryDto>(
    null,
    "race-leaderboard", "Mérés toplista", "Mérés toplista",
    "Toplista a mérési eredmények alapján",
    RaceEntryDto::class, ::RaceEntryDto,
    importService, adminMenuService, component,
    mapOf(),
    controlMode = CONTROL_MODE_NONE,
    permissionControl = PERMISSION_EDIT_RACE,
    importable = false,
    adminMenuPriority = 2, adminMenuIcon = "leaderboard",
    virtualEntity = true,
    savable = true
) {

    override fun fetchOverview(): Iterable<RaceEntryDto> {
        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.USER -> raceService.getBoardForUsers()
            OwnershipType.GROUP -> raceService.getBoardForGroups()
        }
    }

}
