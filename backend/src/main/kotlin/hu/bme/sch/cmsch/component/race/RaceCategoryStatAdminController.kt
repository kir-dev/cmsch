package hu.bme.sch.cmsch.component.race

import hu.bme.sch.cmsch.config.OwnershipType
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.controller.TwoLevelAdminPanel
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_IMPORT_EXPORT
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_RACE_CATEGORY
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin/control/race-category-stat")
@ConditionalOnBean(RaceComponent::class)
class RaceCategoryStatAdminController(
    private val raceCategoryRepository: RaceCategoryRepository,
    private val raceService: RaceService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
) : TwoLevelAdminPanel<RaceCategoryEntity, RaceEntryDto>(
    "race-category-stat", "Eredmény", "Extra mérések",
    "Időmérő eredmények nyers időeredményei",
    RaceCategoryEntity::class,
    RaceEntryDto::class,
    adminMenuService,
    component,
    permissionControl = PERMISSION_EDIT_RACE_CATEGORY,
    adminMenuPriority = 5,
    adminMenuIcon = "leaderboard",
    savable = true
) {

    override fun fetchOverview(): Iterable<RaceCategoryEntity> {
        return raceCategoryRepository.findAll()
    }

    override fun fetchSublist(id: Int): Iterable<RaceEntryDto> {
        val category = raceCategoryRepository.findById(id).orElse(null)
            ?: return listOf()

        return when (startupPropertyConfig.raceOwnershipMode) {
            OwnershipType.USER -> raceService.getBoardForUsers(category.slug, true)
            OwnershipType.GROUP -> raceService.getBoardForGroups(category.slug)
        }
    }

    @ResponseBody
    @GetMapping("/view/{id}/save/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun saveStatsAsCsv(@PathVariable id: Int, auth: Authentication, response: HttpServletResponse): ByteArray {
        val user = auth.getUser()
        if (!permissionControl.validate(user) || !PERMISSION_IMPORT_EXPORT.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"race-category-export-${id}.csv\"")
        return sublistDescriptor.exportToCsv(fetchSublist(id).toList()).toByteArray()
    }

}
