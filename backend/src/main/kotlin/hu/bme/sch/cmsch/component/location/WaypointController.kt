package hu.bme.sch.cmsch.component.location

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.race.*
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/waypoints")
@ConditionalOnBean(RaceComponent::class)
class WaypointController(
    repo: WaypointRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RaceComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    env: Environment
) : OneDeepEntityPage<WaypointEntity>(
    "waypoints",
    WaypointEntity::class, ::WaypointEntity,
    "Jelző", "Jelzők",
    "Fix pontok a térképen",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_LOCATIONS,
    createPermission = StaffPermissions.PERMISSION_CREATE_LOCATIONS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_LOCATIONS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_LOCATIONS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "push_pin",
    adminMenuPriority = 2,
)