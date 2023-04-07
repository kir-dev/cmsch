package hu.bme.sch.cmsch.component.location

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.SimpleEntityPage
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_TRACK_ONE_GROUP
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/track-group")
@ConditionalOnBean(LocationComponent::class)
class TrackOneGroupController(
    private val locationService: LocationService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: LocationComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : SimpleEntityPage<TrackGroupVirtualEntity>(
    "track-group",
    TrackGroupVirtualEntity::class, ::TrackGroupVirtualEntity,
    "Csoportom tagjai", "Csoportom tagjai",
    "A csoportodban lévő emberek. Ameddig valaki nem jelentkezik be, addig itt nem látszik, hogy rendező-e.",

    { locationService.getRecentLocations()
        .map { TrackGroupVirtualEntity(it.id, it.groupName) }.distinct() },

    permission = ImplicitPermissions.PERMISSION_IMPLICIT_HAS_GROUP,

    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    adminMenuIcon = "my_location",
    adminMenuPriority = 4,

    controlActions = mutableListOf(
        ControlAction(
            "Térkép",
            "tracking/{id}",
            "map",
            PERMISSION_TRACK_ONE_GROUP,
            10,
            true
        )
    )
) {

    @GetMapping("/tracking/{id}")
    fun redirectToTracker(@PathVariable id: String): String {
        return "redirect:/admin/control/tracking/${id}"
    }

}
