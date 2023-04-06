package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.SimpleEntityPage
import hu.bme.sch.cmsch.dto.virtual.CheckRatingVirtualEntity
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/check-ratings")
@ConditionalOnBean(TaskComponent::class)
class SoldProductStatsController(
    private val submittedRepository: SubmittedTaskRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TaskComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : SimpleEntityPage<CheckRatingVirtualEntity>(
    "check-ratings",
    CheckRatingVirtualEntity::class, ::CheckRatingVirtualEntity,
    "Pontok ellenőrzése", "Pontok ellenőrzése",
    "Itt azok a beadások láthatóak amik eltérnek a beadásra adható max ponttól vagy a 0 ponttól.",

    { submittedRepository.findAllByScoreGreaterThanAndApprovedIsTrue(0)
        .filter { it.score != (it.task?.maxScore ?: 0) }
        .map { CheckRatingVirtualEntity(it.id, it.groupName, it.score, it.task?.maxScore ?: 0) } },

    permission = StaffPermissions.PERMISSION_EDIT_TASKS,

    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    adminMenuIcon = "fact_check",
    adminMenuPriority = 4,
)