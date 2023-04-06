package hu.bme.sch.cmsch.component.team

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/join-requests")
@ConditionalOnBean(TeamComponent::class)
class TeamController(
    repo: TeamJoinRequestRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TeamComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : OneDeepEntityPage<TeamJoinRequestEntity>(
    "join-requests",
    TeamJoinRequestEntity::class, ::TeamJoinRequestEntity,
    "Kérelem", "Kérelmek",
    "A csoport csatlakozási kérelmek",

    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   ControlPermissions.PERMISSION_CONTROL_TEAM,
    createPermission = ControlPermissions.PERMISSION_CONTROL_TEAM,
    editPermission =   ControlPermissions.PERMISSION_CONTROL_TEAM,
    deletePermission = ControlPermissions.PERMISSION_CONTROL_TEAM,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "group_add",
    adminMenuPriority = 2,
)
