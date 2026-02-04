package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import tools.jackson.databind.ObjectMapper


@Controller
@RequestMapping("/admin/control/tinder-question")
@ConditionalOnBean(CommunitiesComponent::class)
class TinderQuestionController(
    repo: TinderQuestionRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<TinderQuestionEntity>(
    "tinder-question",
    TinderQuestionEntity::class, ::TinderQuestionEntity,
    "Tinder kérdés", "Tinder kérdések",
    "Tinder kérdések kezelése",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
    createPermission = StaffPermissions.PERMISSION_CREATE_COMMUNITIES,
    editPermission = StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
    deletePermission = StaffPermissions.PERMISSION_DELETE_COMMUNITIES,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "mic",
    adminMenuPriority = 3,

    searchSettings = calculateSearchSettings<TinderQuestionEntity>(false)
)