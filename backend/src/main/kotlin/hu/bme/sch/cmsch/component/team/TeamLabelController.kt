package hu.bme.sch.cmsch.component.team

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/team-label")
@ConditionalOnBean(TeamComponent::class)
class TeamLabelController(
    transactionManager: PlatformTransactionManager,
    groupRepository: GroupRepository,
    private val teamLabelRepository: TeamLabelRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    storageService: StorageService,
    component: TeamComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    env: Environment,
): TwoDeepEntityPage<GroupEntity, TeamLabelEntity>(
    "team-label",
    GroupEntity::class,
    TeamLabelEntity::class, ::TeamLabelEntity,
    "Címke", "Címkék",
    "Csapatok Címkéi",

    transactionManager,
    groupRepository,
    teamLabelRepository,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_EDIT_GROUPS,
    createPermission = StaffPermissions.PERMISSION_EDIT_GROUPS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_GROUPS,
    deletePermission = StaffPermissions.PERMISSION_EDIT_GROUPS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "new_label",
    adminMenuPriority = 3,

    outerControlActions = mutableListOf<ControlAction>()
) {
    override fun fetchSublist(id: Int): Iterable<TeamLabelEntity> {
        return teamLabelRepository.findByGroupId(id)
    }


}